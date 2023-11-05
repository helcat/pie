package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie_ZIP_Option</b><br>
 * Only used with "Pie_Zip" object option.<br>
 * "NEVER", Will never create a zip file. Instead, will create encoded image files.<br>
 * "ALWAYS" Will always create a zip file.<br>
 * "ONLY_WHEN_EXTRA_FILES_REQUIRED" Will only create a zip file when encoding needs to be split.
 */
public enum Pie_ZIP_Option {
        NEVER,
        ALWAYS,
        ONLY_WHEN_EXTRA_FILES_REQUIRED
}



