package what.thefcuk.pankxu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import what.thefcuk.pankxu.UrlPolicy.Decision;

/** P3-06 (v3.1): three-way routing, exercised on a plain JVM. */
public class UrlPolicyTest {

    // ---- IN_APP -----------------------------------------------------------------

    @Test
    public void arenaLoadsInApp() {
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai/"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai/agent"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai/side-by-side?model=a&b=2#frag"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai/leaderboard"));
    }

    @Test
    public void hostCaseAndRootDotAreNormalised() {
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://ARENA.AI/Agent"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("HTTPS://Arena.Ai/chat"));
        assertEquals(Decision.IN_APP, UrlPolicy.classify("https://arena.ai./agent"));
    }

    // ---- EXTERNAL ---------------------------------------------------------------

    @Test
    public void helpCentreOpensInTheBrowser() {
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://help.arena.ai/"));
        assertEquals(Decision.EXTERNAL,
                UrlPolicy.classify("https://help.arena.ai/articles/5629909088-terms-of-use"));
    }

    @Test
    public void googleSignInAndOtherSitesGoToTheBrowser() {
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://accounts.google.com/o/oauth2/v2/auth"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://example.com/page"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://evil.com/?next=https://arena.ai"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://evil.com/#https://arena.ai"));
    }

    @Test
    public void unconfirmedSubdomainsAreExternalNotInApp() {
        // Not trusted in-app; they open in the browser instead (v3.1 9.2).
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://www.arena.ai/"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://cdn.arena.ai/"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://api.arena.ai/"));
        // Lookalikes are external too: they open in a browser where the address bar
        // makes the deception obvious, rather than inside our UI.
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://arena.ai.evil.com/agent"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://notarena.ai/agent"));
        assertEquals(Decision.EXTERNAL, UrlPolicy.classify("https://arena.aii/agent"));
    }

    // ---- BLOCK ------------------------------------------------------------------

    @Test
    public void cleartextIsRefused() {
        assertEquals(Decision.BLOCK, UrlPolicy.classify("http://arena.ai/agent"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("http://arena.ai"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("http://help.arena.ai/"));
    }

    @Test
    public void userInfoTrickeryIsRefused() {
        assertEquals(Decision.BLOCK, UrlPolicy.classify("https://arena.ai@evil.com/"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("https://user:pass@arena.ai/"));
    }

    @Test
    public void nonWebSchemesAreRefused() {
        assertEquals(Decision.BLOCK, UrlPolicy.classify("intent://scan/#Intent;scheme=zxing;end"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("javascript:alert(1)"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("file:///etc/passwd"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("content://com.example/x"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("mailto:someone@example.com"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("tel:+10000000000"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("data:text/html,<script>alert(1)</script>"));
    }

    @Test
    public void garbageIsRefused() {
        assertEquals(Decision.BLOCK, UrlPolicy.classify(null));
        assertEquals(Decision.BLOCK, UrlPolicy.classify(""));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("   "));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("not a uri at all"));
        assertEquals(Decision.BLOCK, UrlPolicy.classify("//arena.ai/agent"));
    }

    @Test
    public void inAppHelperAgreesWithClassification() {
        assertEquals(true, UrlPolicy.isAllowedInApp("https://arena.ai/agent"));
        assertEquals(false, UrlPolicy.isAllowedInApp("https://help.arena.ai/"));
        assertEquals(false, UrlPolicy.isAllowedInApp("https://evil.com/"));
        assertEquals(false, UrlPolicy.isAllowedInApp("intent://x"));
    }

    // ----------------------------------------------------------------------------------
    // S0-08 -- allow-list hardening. Each case below is a documented spoofing or parser
    // trick, not a plausible user journey, so the expected value is written next to the
    // rule it protects rather than in a table elsewhere.
    // ----------------------------------------------------------------------------------

    @Test
    public void uppercaseSchemeAndHostStillMatch() {
        assertEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("HTTPS://ARENA.AI/agent"));
        assertEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("HtTpS://Arena.Ai/Leaderboard"));
    }

    @Test
    public void trailingRootDotIsTheSameName() {
        assertEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://arena.ai./agent"));
        assertEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://ARENA.AI./chat"));
    }

    @Test
    public void defaultHttpsPortIsFineOtherPortsAreNotLoadedInApp() {
        assertEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://arena.ai:443/agent"));
        // Another port on the same host is not the published entry point.
        assertEquals(UrlPolicy.Decision.EXTERNAL, UrlPolicy.classify("https://arena.ai:8443/agent"));
    }

    @Test
    public void backslashIsRejectedNotRepaired() {
        // Browsers often treat \ as /; java.net.URI does not. Whatever it does, it must
        // never end up IN_APP, because the two parsers could then disagree about the host.
        assertNotEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://arena.ai\\@evil.example/"));
        assertNotEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https:\\\\arena.ai/agent"));
    }

    @Test
    public void userinfoCannotMasqueradeAsArena() {
        assertEquals(UrlPolicy.Decision.BLOCK, UrlPolicy.classify("https://arena.ai@evil.example/"));
        assertEquals(UrlPolicy.Decision.BLOCK, UrlPolicy.classify("https://evil.example@arena.ai/"));
        assertEquals(UrlPolicy.Decision.BLOCK, UrlPolicy.classify("https://arena.ai:pass@evil.example/"));
    }

    @Test
    public void lookalikeDomainsAreNeverInApp() {
        String[] lookalikes = {
            "https://arena.ai.evil.example/",
            "https://evilarena.ai/",
            "https://arena.ai.evil.com/",
            "https://notarena.ai/",
            "https://arena-ai.example/",
            "https://arena.ais/",
            "https://www.arena.ai.evil.example/",
        };
        for (String url : lookalikes) {
            assertEquals("must not load in app: " + url,
                    UrlPolicy.Decision.EXTERNAL, UrlPolicy.classify(url));
        }
    }

    @Test
    public void nonAsciiHostsAreNeverTreatedAsArena() {
        // Cyrillic small a (U+0430) in the first label, Cyrillic small i (U+0456) in the
        // TLD, and fullwidth latin small a (U+FF41).
        //
        // The expected outcome is BLOCK, not EXTERNAL, and the reason matters:
        // java.net.URI parses these URLs but getHost() returns null for a non-ASCII
        // authority, so the policy cannot even name the host and refuses rather than
        // forwarding. Android's lenient Uri.parse() disagrees - see the S0-08 note in
        // DECISIONS.md and the on-device run of the same inputs.
        String[] unicodeHosts = {
            "https://\u0430rena.ai/agent",
            "https://arena.a\u0456/agent",
            "https://\uFF41rena.ai/agent",
        };
        for (String url : unicodeHosts) {
            assertNotEquals("must never load in app: " + url,
                    UrlPolicy.Decision.IN_APP, UrlPolicy.classify(url));
        }
        assertEquals(UrlPolicy.Decision.BLOCK, UrlPolicy.classify(unicodeHosts[0]));
        assertEquals(UrlPolicy.Decision.BLOCK, UrlPolicy.classify(unicodeHosts[1]));
    }

    @Test
    public void encodedAndDottedTricksStayOutOfTheApp() {
        // Percent-encoded host characters are not decoded into a name we then trust.
        assertNotEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://arena%2Eai.evil.example/"));
        // Double trailing dots do not normalise to the apex.
        assertNotEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://arena.ai../agent"));
        // A leading dot is a different name.
        assertNotEquals(UrlPolicy.Decision.IN_APP, UrlPolicy.classify("https://.arena.ai/agent"));
    }
}
