package what.thefcuk.pankxu;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

/**
 * P3-06 -- the single URL allow-list for this app.
 *
 * <p>Rules: https only; exact host match against {@link #ALLOWED_HOSTS}; everything
 * else is refused and must open in the user's own browser instead.
 *
 * <p>Deliberately conservative:
 * <ul>
 *   <li>Subdomains are NOT trusted implicitly. Nothing under arena.ai has been
 *       confirmed to us as a legitimate app entry point, so each host must be added
 *       here by hand and justified (R5: no invented URLs).</li>
 *   <li>Host equality is exact, so {@code arena.ai.evil.com} and
 *       {@code notarena.ai} are rejected. A {@code endsWith("arena.ai")} test would
 *       accept both -- that is the bug this class exists to avoid.</li>
 *   <li>Userinfo is rejected outright, so {@code https://arena.ai@evil.com/} cannot
 *       masquerade as the trusted host.</li>
 *   <li>Only {@code https}. Cleartext is refused in code as well as in
 *       network_security_config.xml, so neither layer alone is a single point of failure.</li>
 * </ul>
 *
 * <p>Pure Java, no Android types, so it is testable on a plain JVM (see UrlPolicyTest).
 */
public final class UrlPolicy {

    /** Hosts the app is allowed to load. Exact match only. */
    public static final Set<String> ALLOWED_HOSTS = Set.of("arena.ai");

    private UrlPolicy() {
    }

    /**
     * @param candidate a URI string from a shortcut or an inbound intent
     * @return true only if it is an https URL on an allow-listed host
     */
    public static boolean isAllowed(String candidate) {
        if (candidate == null) {
            return false;
        }
        final URI uri;
        try {
            uri = new URI(candidate.trim());
        } catch (URISyntaxException e) {
            return false;
        }

        if (uri.getScheme() == null || !"https".equalsIgnoreCase(uri.getScheme())) {
            return false;
        }
        // Reject https://arena.ai@evil.com/ -- getHost() would return evil.com, but
        // refuse the userinfo form overall rather than rely on that.
        if (uri.getUserInfo() != null) {
            return false;
        }
        final String host = uri.getHost();
        if (host == null) {
            return false;
        }
        return ALLOWED_HOSTS.contains(normaliseHost(host));
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
