package what.thefcuk.pankxu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * S2-04 / X-01..X-03 -- the About screen.
 *
 * <p>Everything the app is willing to say about itself in one place: that it is unofficial,
 * what Arena does with prompts, the privacy policy, the version, the open-source licences it
 * carries, a way to open arena.ai in the real browser, and a diagnostics export the user
 * starts deliberately.
 *
 * <p>No analytics, no network calls, no background work. The diagnostics text is built here
 * and handed to the system share sheet; the app itself never sends it anywhere. It contains
 * version numbers, the device model and the TWA provider package name - nothing that
 * identifies the person, and no Arena content.
 */
public class AboutActivity extends Activity {

    private static final String PRIVACY_URL = "https://arena.ai/privacy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = findViewById(R.id.about_version);
        version.setText(buildVersionLine());

        Button privacy = findViewById(R.id.about_privacy);
        privacy.setOnClickListener(v -> openExternally(PRIVACY_URL));

        Button licenses = findViewById(R.id.about_licenses);
        licenses.setOnClickListener(v -> showLicenses());

        Button browser = findViewById(R.id.about_open_browser);
        browser.setOnClickListener(v -> openExternally(getString(R.string.default_url)));

        Button diagnostics = findViewById(R.id.about_diagnostics);
        diagnostics.setOnClickListener(v -> shareDiagnostics());
    }

    private void showLicenses() {
        startActivity(new Intent(this, LicensesActivity.class));
    }

    /** X-03 / S2-04: user-initiated, redacted, and shared through the user's own chooser. */
    private void shareDiagnostics() {
        String report = buildDiagnostics();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_diagnostics_subject));
        share.putExtra(Intent.EXTRA_TEXT, report);
        try {
            startActivity(Intent.createChooser(share, getString(R.string.about_diagnostics)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.gate_no_browser, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Deliberately plain text. Anything that could identify a person (account names, phone
     * number, serial, e-mail, Arena content) is excluded; if a field is ever added that could,
     * it must be redacted here and noted in THIRD-PARTY-NOTICES/PRIVACY.
     */
    private String buildDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pankxu diagnostics\n");
        sb.append("app version: ").append(versionName()).append(" (")
                .append(versionCode()).append(")\n");
        sb.append("package: ").append(getPackageName()).append('\n');
        sb.append("android: ").append(Build.VERSION.RELEASE)
                .append(" (sdk ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("device: ").append(Build.MANUFACTURER).append(' ').append(Build.MODEL).append('\n');
        sb.append("abi: ").append(Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "?")
                .append('\n');
        sb.append("privacy: ").append(PRIVACY_URL).append('\n');
        return sb.toString();
    }

    /**
     * Reads the version from the installed package rather than BuildConfig: AGP 9 stopped
     * generating BuildConfig by default, and a build flag is one more thing that can drift
     * between the IDE and CI.
     */
    @SuppressWarnings("deprecation")
    private PackageInfo packageInfo() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String versionName() {
        PackageInfo info = packageInfo();
        return info == null || info.versionName == null ? "unknown" : info.versionName;
    }

    @SuppressWarnings("deprecation")
    private long versionCode() {
        PackageInfo info = packageInfo();
        return info == null ? 0L : info.getLongVersionCode();
    }

    private String buildVersionLine() {
        return getString(R.string.about_version_format, versionName(), versionCode());
    }

    private void openExternally(String url) {
        try {
            Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(view);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.gate_no_browser, Toast.LENGTH_LONG).show();
        }
    }
}
