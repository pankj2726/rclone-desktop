package com.pankxu.arenacompanion.core

/**
 * Where a URL is allowed to go. Single source of truth for navigation (dossier 3.2).
 *
 * R1: no requests to arena's internal API paths.
 * R6: auth happens only in a real browser (Custom Tabs), never in the WebView.
 * R7: no session material is ever read, copied or replayed.
 *
 * Pure Kotlin, no Android imports, so the whole contract is JVM-testable.
 */
sealed interface NavDecision {
    data class InApp(val url: String) : NavDecision
    data class CustomTab(val url: String) : NavDecision
    data class Deny(val reason: DenyReason) : NavDecision
}

enum class DenyReason {
    FORBIDDEN_PATH,
    OFF_SCOPE_HOST,
    FORBIDDEN_SCHEME,
    FORBIDDEN_INTENT,
    EXTERNAL_HANDLER,
    NON_STANDARD_PORT,
    MALFORMED,
}

object NavigationPolicy {

    private val ARENA_HOSTS = setOf("arena.ai", "www.arena.ai")

    private val AUTH_HOST_MARKERS = listOf(
        "accounts.google.com",
        "oauth",
        "login",
        "signin",
        "sign-in",
    )

    private val DENIED_PREFIXES = listOf(
        "/api",
        "/nextjs-api",
        "/_next",
        "/c",
        "/agent",
        "/images",
        "/history/search",
    )

    private val AUTH_PATH_MARKERS = listOf(
        "/auth",
        "/login",
        "/signup",
        "/sign-up",
        "/verify-email",
        "/oauth",
    )

    private val ALLOWED_SCHEMES = setOf("https", "http")

    fun decide(rawUrl: String?): NavDecision {
        if (rawUrl.isNullOrBlank()) return NavDecision.Deny(DenyReason.MALFORMED)
        val url = rawUrl.trim()

        val scheme = url.substringBefore(':', missingDelimiterValue = "").lowercase()
        if (scheme.isEmpty()) return NavDecision.Deny(DenyReason.MALFORMED)
        if (scheme == "intent") return NavDecision.Deny(DenyReason.FORBIDDEN_INTENT)
        if (scheme == "mailto" || scheme == "tel") {
            return NavDecision.Deny(DenyReason.EXTERNAL_HANDLER)
        }
        if (scheme !in ALLOWED_SCHEMES) return NavDecision.Deny(DenyReason.FORBIDDEN_SCHEME)

        val host = hostOf(url) ?: return NavDecision.Deny(DenyReason.MALFORMED)
        val path = pathOf(url)
        val port = portOf(url)

        if (host in ARENA_HOSTS && port != null && port != 443 && port != 80) {
            return NavDecision.Deny(DenyReason.NON_STANDARD_PORT)
        }

        if (AUTH_HOST_MARKERS.any { host.contains(it) }) return NavDecision.CustomTab(url)

        if (host !in ARENA_HOSTS) return NavDecision.Deny(DenyReason.OFF_SCOPE_HOST)

        if (DENIED_PREFIXES.any { pathMatches(path, it) }) {
            return NavDecision.Deny(DenyReason.FORBIDDEN_PATH)
        }

        if (AUTH_PATH_MARKERS.any { pathMatches(path, it) }) return NavDecision.CustomTab(url)

        return NavDecision.InApp(url)
    }

    private fun pathMatches(path: String, prefix: String): Boolean =
        path == prefix || path.startsWith("$prefix/")

    private fun hostOf(url: String): String? {
        val afterScheme = url.substringAfter("://", missingDelimiterValue = "")
        if (afterScheme.isEmpty()) return null
        val authority = afterScheme.substringBefore('/').substringBefore('?').substringBefore('#')
        if (authority.isEmpty() || authority.contains('@')) return null
        val host = authority.substringBefore(':').lowercase()
        if (host.isEmpty()) return null
        if (!host.all { it.isLetterOrDigit() || it == '.' || it == '-' }) return null
        if (!host.contains('.')) return null
        return host
    }

    private fun pathOf(url: String): String {
        val afterScheme = url.substringAfter("://", missingDelimiterValue = "")
        val rest = afterScheme.substringAfter('/', missingDelimiterValue = "")
        return "/" + rest.substringBefore('?').substringBefore('#')
    }

    private fun portOf(url: String): Int? {
        val afterScheme = url.substringAfter("://", missingDelimiterValue = "")
        val authority = afterScheme.substringBefore('/')
        if (!authority.contains(':')) return null
        return authority.substringAfter(':').toIntOrNull()
    }
}
