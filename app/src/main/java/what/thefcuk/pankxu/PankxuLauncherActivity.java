package what.thefcuk.pankxu;

import android.net.Uri;

import com.google.androidbrowserhelper.trusted.LauncherActivity;

/**
 * The Trusted Web Activity itself. Not exported: the only way in is
 * {@link LaunchGateActivity}, which validates and routes the URL first.
 *
 * <p>{@link #isTrustedIntentUrl(Uri)} is still overridden so the library's own
 * validation (which would otherwise accept anything matching the declared intent
 * filter) agrees with {@link UrlPolicy}. Defence in depth: two checks, one policy.
 *
 * <p>No JavaScript bridge, no WebView, no page interception. The URL is handed to the
 * browser engine and nothing else is touched (R4).
 */
public class PankxuLauncherActivity extends LauncherActivity {

    @Override
    protected boolean isTrustedIntentUrl(Uri uri) {
        return uri != null && UrlPolicy.isAllowedInApp(uri.toString());
    }
}
