package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie Storage</b><br>
 * Settings for storage. When encoding process is completed the user can select a file storage type.<br>
 * This is used with "setEncoder_storage" within the configuration instead of using the "Pie_Zip" object.<br>
 * "ZIP_FILE will" always produce a zip file.<br>
 * "ZIP_ON_SPLIT_FILE" will always produce a zip file when additional supplemental files are required.<br>
 * "SINGLE_FILES" will always produce encoded image files only even if the encoded file need splitting.<br>
 * Note when using "setEncoder_storage" with "Pie_Storage" additional zip options cannot be set.
 **/
public enum Pie_Storage {
    ZIP_FILE,
    ZIP_ON_SPLIT_FILE,
    SINGLE_FILES
    ;

}


