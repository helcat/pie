package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

/** *****************************************<br>
 * Pie Browser Launch - Tries to show a web page in a browser.
 */
public class Pie_Browser_Launch {
    private static final String[] DEFAULT_BROWSERS = {
            "google-chrome", "firefox", "opera", "konqueror", "epiphany", "seamonkey",
            "galeon", "kazehakase", "mozilla", "netscape"
    };

    public static void openURL(String url) {
        // Attempt to use Desktop API (preferred for modern systems)
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (IOException ignored) { }
            return;
        }

        // Fallback for systems without Desktop API or unsupported action
        String browser = null;
        try {
            browser = findAvailableBrowser(DEFAULT_BROWSERS);
        } catch (IOException ignored) { }
        if (browser == null) {
            return;
        }

        try {
            // Use platform-specific commands for more reliable launching
            if (Pie_Utils.isMac()) {
                Runtime.getRuntime().exec(new String[]{"open", url});

            } else if (Pie_Utils.isWin()) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll", "FileProtocolHandler", url});

            } else {
                Runtime.getRuntime().exec(new String[]{browser, url});

            }
        } catch (IOException ignored) {  }
    }

    private static String findAvailableBrowser(String[] browsers) throws IOException {
        Optional<String> availableBrowser = Arrays.stream(browsers)
                .filter(browser -> {
                    try {
                        return Runtime.getRuntime().exec(new String[]{"which", browser}).waitFor() == 0;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .findFirst();

        return availableBrowser.orElse(null);
    }
}


