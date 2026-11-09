package com.pankxu.arenacompanion.web

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.pankxu.arenacompanion.core.DenyReason
import com.pankxu.arenacompanion.core.NavDecision
import com.pankxu.arenacompanion.core.NavigationPolicy

data class PageState(
    val url: String = HOME,
    val title: String = "",
    val progress: Int = 0,
    val failure: PageFailure? = null,
    val canGoBack: Boolean = false,
) {
    companion object { const val HOME = "https://arena.ai/" }
}

sealed interface PageFailure {
    data class Offline(val host: String) : PageFailure
    data class Server(val code: Int) : PageFailure
    data object Gated : PageFailure
    data class Blocked(val reason: DenyReason) : PageFailure
}

/**
 * The one place a WebView is configured (dossier P2 hardening set).
 * Absent on purpose: file access, content access, multi-window, mixed content,
 * third-party cookies, UA spoofing (R6), and any cookie reading (R7).
 */
class WebViewHost(
    private val activity: Activity,
    private val onState: (PageState) -> Unit,
    private val onNeedBrowser: (String) -> Unit,
) {
    companion object {
        fun userAgentFor(base: String): String =
            base.substringBefore(" ArenaCompanion/") + " ArenaCompanion/1.0"
    }

    private var state = PageState()

    @SuppressLint("SetJavaScriptEnabled")
    fun create(context: android.content.Context): WebView = WebView(context).apply {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = true
            userAgentString = userAgentFor(userAgentString)
            builtInZoomControls = false
            displayZoomControls = false
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)
        CookieManager.getInstance().setAcceptCookie(true)

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                intercept(request.url.toString())

            @Deprecated("API < 24 parity; minSdk is 26")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = intercept(url)

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                push(state.copy(url = url ?: state.url, progress = 0, failure = null))
            }

            override fun onPageFinished(view: WebView, url: String?) {
                push(
                    state.copy(
                        url = url ?: state.url,
                        title = view.title.orEmpty(),
                        progress = 100,
                        canGoBack = view.canGoBack(),
                    )
                )
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                if (request.isForMainFrame) {
                    val host = runCatching { Uri.parse(request.url.toString()).host }.getOrNull()
                    push(state.copy(failure = PageFailure.Offline(host.orEmpty())))
                }
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                response: WebResourceResponse,
            ) {
                if (!request.isForMainFrame) return
                val code = response.statusCode
                push(
                    state.copy(
                        failure = when (code) {
                            401, 403, 429 -> PageFailure.Gated
                            in 500..599 -> PageFailure.Server(code)
                            else -> null
                        }
                    )
                )
            }
        }
        loadUrl(PageState.HOME)
    }

    private fun intercept(url: String): Boolean = when (val d = NavigationPolicy.decide(url)) {
        is NavDecision.InApp -> false
        is NavDecision.CustomTab -> { onNeedBrowser(d.url); true }
        is NavDecision.Deny -> {
            if (d.reason == DenyReason.EXTERNAL_HANDLER) {
                runCatching {
                    activity.startActivity(
                        android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                    )
                }
                true
            } else {
                push(state.copy(failure = PageFailure.Blocked(d.reason)))
                true
            }
        }
    }

    private fun push(next: PageState) { state = next; onState(next) }
}
