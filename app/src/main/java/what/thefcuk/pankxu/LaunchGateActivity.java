package what.thefcuk.pankxu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The exported entry point. Everything inbound passes through here first.
 *
 * <p>Responsibilities, in order:
 * <ol>
 *   <li>P3-12 / X-10 -- offline pre-check with a Retry screen before the browser is
 *       asked to load anything.</li>
 *   <li>X-03 -- one-time disclosure that Side-by-Side and Direct prompts are collected
 *       by Arena for research, with a link to Arena's privacy page.</li>
 *   <li>P3-06 -- route the URL: arena.ai in the TWA, other https to the user's browser,
 *       everything else refused.</li>
 * </ol>
 *
 * <p>Keeping this separate from {@code PankxuLauncherActivity} means the TWA activity
 * itself is never exported and never receives unvalidated input.
 */
public class LaunchGateActivity extends Activity {

    private static final String PREFS = "gate";
    private static final String KEY_DISCLOSURE_SHOWN = "disclosure_shown_v1";
    private static final String ARENA_PRIVACY_URL = "https://arena.ai/privacy";

    private static final String TAG = "PankxuGate";

    private boolean waitingForNetwork;
    /** Set when the user accepts the Custom Tab fallback, so the notice is not a loop. */
    private boolean proceedingWithoutTwa;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);
        status = findViewById(R.id.gate_status);
        Button retry = findViewById(R.id.gate_retry);
        retry.setOnClickListener(v -> proceed());
        Button about = findViewById(R.id.gate_about);
        about.setOnClickListener(v ->
                startActivity(new Intent(LaunchGateActivity.this, AboutActivity.class)));
        maybeShowDisclosureThenProceed();
    }

    private void maybeShowDisclosureThenProceed() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_DISCLOSURE_SHOWN, false)) {
            proceed();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.gate_disclosure_title)
                .setMessage(R.string.gate_disclosure_body)
                .setCancelable(false)
                .setPositiveButton(R.string.gate_disclosure_ok, (d, w) -> {
                    prefs.edit().putBoolean(KEY_DISCLOSURE_SHOWN, true).apply();
                    proceed();
                })
                .setNeutralButton(R.string.gate_disclosure_privacy, (d, w) -> {
                    prefs.edit().putBoolean(KEY_DISCLOSURE_SHOWN, true).apply();
                    openExternally(ARENA_PRIVACY_URL);
                    proceed();
                })
                .show();
    }

    private void proceed() {
        final String requested = extractRequestedUrl();
        final UrlPolicy.Decision decision = UrlPolicy.classify(requested);

        if (decision == UrlPolicy.Decision.BLOCK) {
            // A non-web scheme or a malformed URL. Say so and leave; never forward it.
            Toast.makeText(this, R.string.gate_rejected, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (decision == UrlPolicy.Decision.EXTERNAL) {
            openExternally(requested);
            finish();
            return;
        }

        if (!hasNetwork()) {
            showOffline();
            return;
        }

        // A TWA needs a browser that declares the TrustedWebActivities category. Without
        // one the library degrades to a Custom Tab, which is a browser tab with a URL bar
        // rather than the app-like experience. Say so once instead of degrading silently.
        // The browser toolbar is visible whenever Arena has not published an assetlinks.json
        // entry for this package, which is the case today regardless of what browser is
        // installed. Say that once per launch instead of guessing which browser is present:
        // Android 11+ package visibility makes a TWA-capability probe unreliable without
        // declaring queries for Chrome's internal intent, and a wrong probe produced a false
        // "Chrome is not installed" on a device where Chrome was installed (D-30).
        if (!proceedingWithoutTwa) {
            showNotice(R.string.gate_toolbar_notice);
            return;
        }

        // IN_APP: hand to the Trusted Web Activity launcher, which re-validates.
        Intent launch = new Intent(this, PankxuLauncherActivity.class);
        launch.putExtra(Intent.EXTRA_TEXT, requested);
        launch.setData(Uri.parse(requested));
        startActivity(launch);
        finish();
    }

    /** @return the inbound URL if it is an arena.ai URL, otherwise DEFAULT_URL. */
    private String extractRequestedUrl() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            return intent.getData().toString();
        }
        return getString(R.string.default_url);
    }

    /**
     * No TWA host installed. Explain the difference and let the user continue in a
     * browser tab deliberately. Re-checked on every launch, so installing Chrome later
     * takes effect without clearing app data.
     */
    private void showNotice(int message) {
        Log.i(TAG, "routing notice shown before first load");
        status.setText(message);
        status.setVisibility(View.VISIBLE);
        Button retry = findViewById(R.id.gate_retry);
        retry.setText(R.string.gate_continue);
        retry.setOnClickListener(v -> {
            proceedingWithoutTwa = true;
            retry.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            retry.setText(R.string.gate_retry);
            retry.setOnClickListener(w -> proceed());
            proceed();
        });
        retry.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        waitingForNetwork = true;
        status.setText(R.string.gate_offline);
        status.setVisibility(View.VISIBLE);
        findViewById(R.id.gate_retry).setVisibility(View.VISIBLE);
    }

    private void openExternally(String url) {
        try {
            Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(view);
        } catch (ActivityNotFoundException e) {
            // No browser installed. Tell the user rather than crash (P3-05).
            Toast.makeText(this, R.string.gate_no_browser, Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return true; // cannot tell; do not block the user
        }
        Network active = cm.getActiveNetwork();
        if (active == null) {
            return false;
        }
        NetworkCapabilities caps = cm.getNetworkCapabilities(active);
        return caps != null
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitingForNetwork) {
            waitingForNetwork = false;
            proceed();
        }
    }
}
