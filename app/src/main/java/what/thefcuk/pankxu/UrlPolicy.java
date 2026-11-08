package what.thefcuk.pankxu;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

/**
 * P3-06 (as amended by roadmap v3.1 section 9.2) -- URL routing policy.
 *
 * <p>v3.1 replaced the old binary allow/deny rule with three explicit outcomes:
 *
 * <ul>
 *   <li>{@link Decision#IN_APP} -- arena.ai itself, loaded inside the TWA.</li>
 *   <li>{@link Decision#EXTERNAL} -- anything else over https (help.arena.ai, Terms,
 *       Privacy, Google sign-in, external links). Handed to the user's own browser,
 *       where the address bar shows exactly where they are going.</li>
 *   <li>{@link Decision#BLOCK} -- everything else: cleartext, non-web schemes,
 *       malformed input, userinfo trickery.</li>
 * </ul>
 *
 * <p>Why EXTERNAL exists: the library's default is to fall back to DEFAULT_URL for an
 * untrusted URL, which would silently dump a reader who taps a Terms link onto the
 * agent page. Sending it to the browser is both honest and what v3.1 asks for.
 *
 * <p>Host handling:
 * <ul>
 *   <li>Exact, case-insensitive match. Never a suffix test: {@code endsWith("arena.ai")}
 *       would accept {@code notarena.ai} and {@code arena.ai.evil.com}.</li>
 *   <li>Subdomains are NOT trusted in-app. Nothing under arena.ai has been confirmed to
 *       us as an app entry point (R5), so only the apex is loaded in the TWA.</li>
 *   <li>Userinfo is rejected outright, so {@code https://arena.ai@evil.com/} cannot
 *       masquerade.</li>
 * </ul>
 *
 * <p>Pure Java, no Android types, so it is testable on a plain JVM.
 */
public final class UrlPolicy {

    public enum Decision {
        /** Load inside the Trusted Web Activity. */
        IN_APP,
        /** Hand to the user's default browser via ACTION_VIEW. */
        EXTERNAL,
        /** Never load, never launch anything. */
        BLOCK
    }

    /** Origins rendered inside the app. Exact match only. */
    private static final Set<String> IN_APP_HOSTS = Set.of("arena.ai");

    private UrlPolicy() {
    }

    /**
     * @param candidate a URI string from a shortcut, an inbound intent or a tap
     * @return what the app should do with it; never null
     */
    public static Decision classify(String candidate) {
        if (candidate == null) {
            return Decision.BLOCK;
        }
        final URI uri;
        try {
            uri = new URI(candidate.trim());
        } catch (URISyntaxException e) {
            return Decision.BLOCK;
        }

        final String scheme = uri.getScheme();
        if (scheme == null || !"https".equalsIgnoreCase(scheme)) {
            // Cleartext and every non-web scheme (intent:, javascript:, file:,
            // content:, data:, tel:, mailto:) is refused rather than forwarded.
            // Launching arbitrary external handlers from a link is its own risk.
            return Decision.BLOCK;
        }
        if (uri.getUserInfo() != null) {
            return Decision.BLOCK;
        }
        // S0-08: only the default https port may be loaded in-app. arena.ai on another port
        // is not arena.ai's published entry point, and letting the app frame arbitrary
        // services on a known host is exactly the kind of allowance that ages badly.
        final int port = uri.getPort();
        if (port != -1 && port != 443) {
            return Decision.EXTERNAL;
        }
        final String host = uri.getHost();
        if (host == null) {
            return Decision.BLOCK;
        }
        final String normalised = normaliseHost(host);
        if (normalised.isEmpty()) {
            return Decision.BLOCK;
        }
        return IN_APP_HOSTS.contains(normalised) ? Decision.IN_APP : Decision.EXTERNAL;
    }

    /** Convenience for the TWA launcher's inbound validation. */
    public static boolean isAllowedInApp(String candidate) {
        return classify(candidate) == Decision.IN_APP;
    }

    private static String normaliseHost(String host) {
        String h = host.toLowerCase(Locale.ROOT);
        // A single trailing dot is a valid, root-anchored form of the same name.
        if (h.endsWith(".") && h.length() > 1) {
            h = h.substring(0, h.length() - 1);
        }
        return h;
    }
}
