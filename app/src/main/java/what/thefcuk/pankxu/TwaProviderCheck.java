package what.thefcuk.pankxu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Whether an installed browser can host a Trusted Web Activity.
 *
 * <p><b>Fixed 2026-11-08 after checking Chrome's own manifest on the device.</b> The first
 * version of this class queried *services* for
 * {@code androidx.browser.trusted.category.TrustedWebActivities}. That category is declared
 * on Chrome's {@code ManageTrustedWebActivityDataActivity}, an activity - so the query
 * returned nothing, the gate announced "Chrome is not installed" on a device where Chrome
 * 156 was installed and signed in, and the message the user saw was simply false.
 *
 * <p>The check is a capability test only. It does not mean the user gets a toolbar-free app:
 * Arena publishes no {@code assetlinks.json} for this package, so the browser shows its
 * toolbar even in a browser that supports TWAs. See {@link #toolbarAlwaysVisible()}.
 */
final class TwaProviderCheck {

    private static final String CATEGORY_TWA =
            "androidx.browser.trusted.category.TrustedWebActivities";

    private TwaProviderCheck() {
    }

    /** @return true if at least one installed browser can host a Trusted Web Activity. */
    static boolean isProviderInstalled(Context context) {
        return !providers(context).isEmpty();
    }

    /**
     * The browser toolbar cannot be hidden while Arena has not published a digital asset
     * link statement for this package. That is a property of Arena's site, not of the
     * device, so the app says so instead of implying that installing Chrome fixes it.
     */
    static boolean toolbarAlwaysVisible() {
        return true; // until arena.ai serves an assetlinks.json entry for this app
    }

    /** Package name of the first TWA-capable browser, for logging. Never shown to the user. */
    static String providerPackage(Context context) {
        List<ResolveInfo> found = providers(context);
        if (found.isEmpty() || found.get(0).activityInfo == null) {
            return "none";
        }
        return found.get(0).activityInfo.packageName;
    }

    private static List<ResolveInfo> providers(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return List.of();
        }
        // Activities, not services: that is where the category actually lives.
        List<ResolveInfo> found = pm.queryIntentActivities(
                new Intent(CATEGORY_TWA), PackageManager.MATCH_DEFAULT_ONLY);
        return found == null ? List.of() : found;
    }
}
