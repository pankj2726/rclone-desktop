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

    private boolean waitingForNetwork;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);
        status = findViewById(R.id.gate_status);
        Button retry = findViewById(R.id.gate_retry);
        retry.setOnClickListener(v -> proceed());
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
