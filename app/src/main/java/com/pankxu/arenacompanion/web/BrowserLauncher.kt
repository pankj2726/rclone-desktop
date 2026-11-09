package com.pankxu.arenacompanion.web

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

/**
 * H1+ partial Custom Tab (dossier PART 4). Sign-in happens in a real browser, never
 * in our WebView (R6); we never read or copy what comes back (R7).
 */
object BrowserLauncher {

    fun launch(activity: Activity, url: String, partial: Boolean = true) {
        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(false)
            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().build())
        // Note: setInitialActivityHeightPercent was removed in browser 1.9.0.
        // Full Custom Tab is used instead.
        runCatching { builder.build().launchUrl(activity, Uri.parse(url)) }
            .onFailure {
                runCatching { activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            }
    }
}
