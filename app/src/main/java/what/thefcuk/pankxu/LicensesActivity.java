package what.thefcuk.pankxu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * S2-04 / X-24 -- the open-source licence text the app actually links.
 *
 * <p>The list is read from a raw resource so the same wording is versioned with the code and
 * can be diffed in review. It mirrors THIRD-PARTY-NOTICES.md; if the dependencies change,
 * both must change (scripts/check-notices.py fails the build when they drift).
 */
public class LicensesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        TextView body = findViewById(R.id.licenses_body);
        body.setText(Notices.text(this));
    }
}
