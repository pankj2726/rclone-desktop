package what.thefcuk.pankxu;

import static org.junit.Assert.assertEquals;

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
}
