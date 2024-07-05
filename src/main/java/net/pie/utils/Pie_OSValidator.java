package net.pie.utils;

import java.util.Locale;

/** ***********************************************<br>
 * Validate OS
 * This jar has no dependencies.
 */
public class Pie_OSValidator {
    private String OS = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    private String bit = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

    public Pie_OSValidator () {
    }

    public boolean isWindows() {
        return OS.contains("win");
    }

    public boolean isMac() {
        return OS.contains("mac") || OS.contains("osx");
    }

    public boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public boolean isSolaris() {
        return OS.contains("sunos");
    }

    public boolean is64Bit() {
        return bit.contains("64");
    }

}
