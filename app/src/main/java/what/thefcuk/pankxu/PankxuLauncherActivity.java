package what.thefcuk.pankxu;

import android.net.Uri;

import com.google.androidbrowserhelper.trusted.LauncherActivity;

/**
 * The app's only activity.
 *
 * <p>The library already refuses untrusted inbound https URLs by comparing them with
 * the DEFAULT_URL origin and any declared BROWSABLE intent filters. Overriding
 * {@link #isTrustedIntentUrl(Uri)} routes that decision through {@link UrlPolicy}, so
 * the allow-list lives in exactly one place and is covered by JVM unit tests.
 *
 * <p>No JavaScript bridge, no WebView, no interception of page content: the URL is
 * handed to the browser engine and nothing else is touched (R4).
 */
public class PankxuLauncherActivity extends LauncherActivity {

    @Override
    protected boolean isTrustedIntentUrl(Uri uri) {
        return uri != null && UrlPolicy.isAllowed(uri.toString());
    }
}
