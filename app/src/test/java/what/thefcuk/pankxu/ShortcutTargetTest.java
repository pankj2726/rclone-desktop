package what.thefcuk.pankxu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * S0-07(b) -- every shortcut target must resolve, for BOTH build types.
 *
 * <p>This cannot be a device test in CI (no emulator here), so it is a source-level check
 * with an explicit per-flavour expectation:
 *
 * <ul>
 *   <li>{@code src/main/res/xml/shortcuts.xml} must name the release applicationId;</li>
 *   <li>{@code src/debug/res/xml/shortcuts.xml} must override it with the {@code .debug} id,
 *       and must otherwise be identical.</li>
 * </ul>
 *
 * <p>Motivated by two device failures on 2026-11-08: a hard-coded release id made every
 * shortcut throw {@code ActivityNotFoundException} on the debug build, and omitting the
 * package turned the intent implicit and opened a chooser instead (D-26).
 */
public class ShortcutTargetTest {

    private static final String RELEASE_ID = "what.thefcuk.pankxu";
    private static final String DEBUG_ID = "what.thefcuk.pankxu.debug";
    private static final String GATE = "what.thefcuk.pankxu.LaunchGateActivity";

    /** The Gradle build runs tests with the module directory as working directory. */
    private static File find(String relative) {
        File fromModule = new File(relative);
        if (fromModule.exists()) {
            return fromModule;
        }
        return new File("app/" + relative);
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

    private static List<String> attribute(String xml, String name) {
        List<String> values = new ArrayList<>();
        Matcher m = Pattern.compile(name + "=\"([^\"]+)\"").matcher(xml);
        while (m.find()) {
            values.add(m.group(1));
        }
        return values;
    }

    @Test
    public void releaseShortcutsTargetTheReleasePackage() throws IOException {
        File file = find("src/main/res/xml/shortcuts.xml");
        assertTrue("missing " + file, file.exists());
        String xml = read(file);

        List<String> packages = attribute(xml, "android:targetPackage");
        assertEquals("four shortcuts expected", 4, packages.size());
        for (String pkg : packages) {
            assertEquals("release shortcuts must target the release id", RELEASE_ID, pkg);
        }
        for (String cls : attribute(xml, "android:targetClass")) {
            assertEquals(GATE, cls);
        }
        assertTrue("shortcut urls must all be arena.ai",
                attribute(xml, "android:data").stream().allMatch(u -> u.startsWith("https://arena.ai/")));
    }

    @Test
    public void debugShortcutsOverrideToTheDebugPackage() throws IOException {
        File file = find("src/debug/res/xml/shortcuts.xml");
        assertNotNull(file);
        assertTrue("debug override missing - debug builds will fail to start shortcuts: " + file,
                file.exists());
        String xml = read(file);

        List<String> packages = attribute(xml, "android:targetPackage");
        assertEquals("four shortcuts expected", 4, packages.size());
        for (String pkg : packages) {
            assertEquals("debug shortcuts must target the debug id", DEBUG_ID, pkg);
        }
    }

    @Test
    public void bothBuildTypesAgreeOnUrlsAndClasses() throws IOException {
        File release = find("src/main/res/xml/shortcuts.xml");
        File debug = find("src/debug/res/xml/shortcuts.xml");
        assertEquals(attribute(read(release), "android:data"), attribute(read(debug), "android:data"));
        assertEquals(attribute(read(release), "android:shortcutId"),
                attribute(read(debug), "android:shortcutId"));
        assertEquals(attribute(read(release), "android:targetClass"),
                attribute(read(debug), "android:targetClass"));
    }

    @Test
    public void everyShortcutUrlIsAllowedInAppByThePolicy() throws IOException {
        String xml = read(find("src/main/res/xml/shortcuts.xml"));
        List<String> urls = attribute(xml, "android:data");
        assertEquals(4, urls.size());
        for (String url : urls) {
            assertEquals("shortcut url must classify as IN_APP: " + url,
                    UrlPolicy.Decision.IN_APP, UrlPolicy.classify(url));
        }
    }
}
