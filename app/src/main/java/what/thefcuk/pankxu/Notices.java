package what.thefcuk.pankxu;

import android.content.Context;
import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Reads the bundled licence notice text. Small enough to keep out of the activities. */
final class Notices {

    private Notices() {
    }

    static String text(Context context) {
        Resources res = context.getResources();
        try (InputStream in = res.openRawResource(R.raw.notices)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return context.getString(R.string.about_licenses_unavailable);
        }
    }
}
