package what.thefcuk.pankxu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * S0-07(c) -- the theme/colour pairing that broke on a device on 2026-11-08.
 *
 * <p>The gate inherited {@code Theme.Material} (the DARK platform theme) while
 * {@code @color/bar} was {@code #FFFFFFFF} in light mode, so every label rendered white on
 * white: present in the view tree, invisible on screen (D-24). Lint does not see it, the
 * layout is valid, and the strings resolve - only a screenshot caught it.
 *
 * <p>This test encodes the rule so it cannot come back: in each configuration the theme
 * parent's lightness must match the background colour's lightness.
 */
public class ThemeContrastTest {

    /** Perceived luminance above this counts as a light background. */
    private static final double LIGHT_THRESHOLD = 0.5;

    private static File find(String relative) {
        File fromModule = new File(relative);
        return fromModule.exists() ? fromModule : new File("app/" + relative);
    }

    private static String read(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            int offset = 0;
            int n;
            while (offset < buffer.length && (n = in.read(buffer, offset, buffer.length - offset)) > 0) {
                offset += n;
            }
            return new String(buffer, 0, offset, StandardCharsets.UTF_8);
        }
    }

    private static String themeParent(String themesDir) throws IOException {
        File f = find("src/main/res/" + themesDir + "/themes.xml");
        assertTrue("missing " + f, f.exists());
        Matcher m = Pattern.compile("name=\"Theme\\.Pankxu\"\\s+parent=\"@android:style/([^\"]+)\"")
                .matcher(read(f));
        assertTrue("Theme.Pankxu not found in " + f, m.find());
        return m.group(1);
    }

    private static String barColour(String dir) throws IOException {
        File f = find("src/main/res/" + dir + "/colors.xml");
        assertTrue("missing " + f, f.exists());
        Matcher m = Pattern.compile("name=\"bar\">#(?:FF)?([0-9A-Fa-f]{6})<").matcher(read(f));
        assertTrue("colour 'bar' not found in " + f, m.find());
        return m.group(1);
    }

    private static boolean isLight(String hexRgb) {
        int r = Integer.parseInt(hexRgb.substring(0, 2), 16);
        int g = Integer.parseInt(hexRgb.substring(2, 4), 16);
        int b = Integer.parseInt(hexRgb.substring(4, 6), 16);
        // Same weighting Android uses for luminance.
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
        return luminance > LIGHT_THRESHOLD;
    }

    @Test
    public void lightConfigurationUsesALightThemeParent() throws IOException {
        boolean lightBackground = isLight(barColour("values"));
        boolean lightParent = themeParent("values").contains("Light");
        assertTrue("values/: background is " + (lightBackground ? "light" : "dark")
                        + " but parent is " + themeParent("values")
                        + " - text colour will not match the background (D-24)",
                lightBackground == lightParent);
    }

    @Test
    public void nightConfigurationUsesADarkThemeParent() throws IOException {
        boolean lightBackground = isLight(barColour("values-night"));
        boolean lightParent = themeParent("values-night").contains("Light");
        assertTrue("values-night/: background is " + (lightBackground ? "light" : "dark")
                        + " but parent is " + themeParent("values-night"),
                lightBackground == lightParent);
    }

    @Test
    public void theTwoConfigurationsActuallyDiffer() throws IOException {
        assertNotNull(barColour("values"));
        assertTrue("values and values-night should use different bar colours, otherwise one of"
                        + " them is mismatched by construction",
                !barColour("values").equals(barColour("values-night")));
    }
}
