package what.thefcuk.pankxu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Whether a Trusted Web Activity host is installed (in practice: Chrome, or a
 * Chromium build that declares the TWA category).
 *
 * <p>The TWA experience -- no URL bar, custom splash screen, the app's own icon in
 * recents -- only exists when a browser declares
 * {@code androidx.browser.trusted.category.TrustedWebActivities}. Without one,
 * androidbrowserhelper silently degrades to a Custom Tab in whatever browser happens
 * to be installed, which looks like a browser tab rather than an app. That degradation
 * was invisible to the user before this class existed; the gate now names it.
 *
 * <p>Querying is safe and needs no permission. Package visibility on Android 11+ does
 * not hide this: androidbrowserhelper's own manifest already declares the matching
 * {@code <queries>} entry, and it merges into our manifest.
 */
final class TwaProviderCheck {

    private static final String ACTION_TWA_SERVICE =
            "androidx.browser.trusted.category.TrustedWebActivities";

    private TwaProviderCheck() {
    }

    /** @return true if at least one installed app can host a Trusted Web Activity. */
    static boolean isProviderInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return true; // cannot tell; do not nag
        }
        Intent intent = new Intent(ACTION_TWA_SERVICE);
        List<ResolveInfo> providers = pm.queryIntentServices(intent, 0);
        return providers != null && !providers.isEmpty();
    }

    /** Package name of the first TWA provider, for logging. Never shown to the user. */
    static String providerPackage(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return "unknown";
        }
        List<ResolveInfo> providers =
                pm.queryIntentServices(new Intent(ACTION_TWA_SERVICE), 0);
        if (providers == null || providers.isEmpty() || providers.get(0).serviceInfo == null) {
            return "none";
        }
        return providers.get(0).serviceInfo.packageName;
    }
}
