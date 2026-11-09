package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.DenyReason
import com.pankxu.arenacompanion.core.NavDecision
import com.pankxu.arenacompanion.core.NavigationPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** T-SCOPE and T-AUTH as tests (dossier P2 exit criteria). Pure JVM. */
class NavigationPolicyTest {

    private fun deny(url: String?): DenyReason {
        val d = NavigationPolicy.decide(url)
        assertTrue("expected Deny for $url but got $d", d is NavDecision.Deny)
        return (d as NavDecision.Deny).reason
    }

    private fun inApp(url: String) = assertTrue(NavigationPolicy.decide(url) is NavDecision.InApp)
    private fun customTab(url: String) = assertTrue(NavigationPolicy.decide(url) is NavDecision.CustomTab)

    @Test fun homeIsInApp() = inApp("https://arena.ai/")
    @Test fun wwwHostIsInApp() = inApp("https://www.arena.ai/")
    @Test fun leaderboardIsInApp() = inApp("https://arena.ai/leaderboard")
    @Test fun sideBySideIsInApp() = inApp("https://arena.ai/side-by-side")
    @Test fun faqIsInApp() = inApp("https://arena.ai/faq")
    @Test fun blogIsInApp() = inApp("https://arena.ai/blog")
    @Test fun queryStringIsKept() = inApp("https://arena.ai/leaderboard?tab=text")

    @Test fun apiIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/api/x"))
    @Test fun apiRootIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/api"))
    @Test fun nextjsApiIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/nextjs-api/chat"))
    @Test fun nextDataIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/_next/static/x"))
    @Test fun cPrefixIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/c/abc"))
    @Test fun agentIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/agent/run"))
    @Test fun imagesIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/images/x.png"))
    @Test fun historySearchIsDenied() = assertEquals(DenyReason.FORBIDDEN_PATH, deny("https://arena.ai/history/search?q=me"))
    @Test fun apiaryIsNotApi() = inApp("https://arena.ai/apiary")

    @Test fun authPathIsCustomTab() = customTab("https://arena.ai/auth/login")
    @Test fun loginPathIsCustomTab() = customTab("https://arena.ai/login")
    @Test fun signupIsCustomTab() = customTab("https://arena.ai/signup")
    @Test fun verifyEmailIsCustomTab() = customTab("https://arena.ai/verify-email?token=x")
    @Test fun googleIsCustomTab() = customTab("https://accounts.google.com/o/oauth2/auth?client=x")

    @Test fun javascriptIsDenied() = assertEquals(DenyReason.FORBIDDEN_SCHEME, deny("javascript:alert(1)"))
    @Test fun dataUriIsDenied() = assertEquals(DenyReason.FORBIDDEN_SCHEME, deny("data:text/html,<b>x"))
    @Test fun fileIsDenied() = assertEquals(DenyReason.FORBIDDEN_SCHEME, deny("file:///etc/passwd"))
    @Test fun intentIsDenied() = assertEquals(DenyReason.FORBIDDEN_INTENT, deny("intent://x#Intent;scheme=https;end"))
    @Test fun mailtoIsExternal() = assertEquals(DenyReason.EXTERNAL_HANDLER, deny("mailto:a@b.c"))
    @Test fun telIsExternal() = assertEquals(DenyReason.EXTERNAL_HANDLER, deny("tel:+100"))

    @Test fun otherHostIsDenied() = assertEquals(DenyReason.OFF_SCOPE_HOST, deny("https://evil.example/arena.ai"))
    @Test fun userinfoSpoofIsDenied() { deny("https://arena.ai@evil.example/") }
    @Test fun lookalikeHostIsDenied() = assertEquals(DenyReason.OFF_SCOPE_HOST, deny("https://arena.ai.evil.example/"))
    @Test fun nonStandardPortIsDenied() = assertEquals(DenyReason.NON_STANDARD_PORT, deny("https://arena.ai:8443/api/x"))
    @Test fun explicit443IsFine() = inApp("https://arena.ai:443/leaderboard")

    @Test fun nullIsDenied() = assertEquals(DenyReason.MALFORMED, deny(null))
    @Test fun emptyIsDenied() = assertEquals(DenyReason.MALFORMED, deny(""))
    @Test fun noSchemeIsDenied() = assertEquals(DenyReason.MALFORMED, deny("arena.ai/leaderboard"))
    @Test fun bareHostIsDenied() = assertEquals(DenyReason.MALFORMED, deny("https://localhost/"))
}
