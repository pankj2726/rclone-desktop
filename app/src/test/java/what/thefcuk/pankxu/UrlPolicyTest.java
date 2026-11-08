package what.thefcuk.pankxu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** P3-06: the allow-list, exercised on a plain JVM. */
public class UrlPolicyTest {

    @Test
    public void allowsArenaOverHttps() {
        assertTrue(UrlPolicy.isAllowed("https://arena.ai"));
        assertTrue(UrlPolicy.isAllowed("https://arena.ai/"));
        assertTrue(UrlPolicy.isAllowed("https://arena.ai/agent"));
        assertTrue(UrlPolicy.isAllowed("https://arena.ai/side-by-side?model=a&b=2#frag"));
        assertTrue(UrlPolicy.isAllowed("https://arena.ai/leaderboard"));
    }

    @Test
    public void hostsAreComparedCaseInsensitively() {
        assertTrue(UrlPolicy.isAllowed("https://ARENA.AI/Agent"));
        assertTrue(UrlPolicy.isAllowed("HTTPS://Arena.Ai/chat"));
    }

    @Test
    public void rejectsCleartext() {
        assertFalse(UrlPolicy.isAllowed("http://arena.ai/agent"));
        assertFalse(UrlPolicy.isAllowed("http://arena.ai"));
    }

    @Test
    public void rejectsLookalikeHosts() {
        assertFalse(UrlPolicy.isAllowed("https://arena.ai.evil.com/agent"));
        assertFalse(UrlPolicy.isAllowed("https://notarena.ai/agent"));
        assertFalse(UrlPolicy.isAllowed("https://arena.aii/agent"));
        assertFalse(UrlPolicy.isAllowed("https://evil.com/?next=https://arena.ai"));
        assertFalse(UrlPolicy.isAllowed("https://evil.com/#https://arena.ai"));
    }

    @Test
    public void rejectsUnconfirmedSubdomains() {
        // Not trusted implicitly. Adding one means editing ALLOWED_HOSTS with a
        // source for the URL (R5).
        assertFalse(UrlPolicy.isAllowed("https://www.arena.ai/"));
        assertFalse(UrlPolicy.isAllowed("https://cdn.arena.ai/"));
        assertFalse(UrlPolicy.isAllowed("https://api.arena.ai/"));
    }

    @Test
    public void rejectsUserInfoTrickery() {
        assertFalse(UrlPolicy.isAllowed("https://arena.ai@evil.com/"));
        assertFalse(UrlPolicy.isAllowed("https://user:pass@arena.ai/"));
    }

    @Test
    public void rejectsOtherSchemes() {
        assertFalse(UrlPolicy.isAllowed("intent://scan/#Intent;scheme=zxing;end"));
        assertFalse(UrlPolicy.isAllowed("javascript:alert(1)"));
        assertFalse(UrlPolicy.isAllowed("file:///etc/passwd"));
        assertFalse(UrlPolicy.isAllowed("content://com.example/x"));
        assertFalse(UrlPolicy.isAllowed("mailto:someone@example.com"));
        assertFalse(UrlPolicy.isAllowed("tel:+10000000000"));
        assertFalse(UrlPolicy.isAllowed("data:text/html,<script>alert(1)</script>"));
    }

    @Test
    public void rejectsGarbage() {
        assertFalse(UrlPolicy.isAllowed(null));
        assertFalse(UrlPolicy.isAllowed(""));
        assertFalse(UrlPolicy.isAllowed("   "));
        assertFalse(UrlPolicy.isAllowed("not a uri at all"));
        assertFalse(UrlPolicy.isAllowed("//arena.ai/agent"));
    }

    @Test
    public void acceptsRootAnchoredForm() {
        assertTrue(UrlPolicy.isAllowed("https://arena.ai./agent"));
    }
}
