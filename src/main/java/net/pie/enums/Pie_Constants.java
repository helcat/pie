package net.pie.enums;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

import java.nio.charset.StandardCharsets;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used internally for default settings.
 **/
public enum Pie_Constants {
    PIE_VERSION (0, "v1.3"),

    MIN_PROTECTED_SIZE (50, "MIN"),
    MAX_PROTECTED_SIZE (15000, "MAX"),
    MAX_PROTCTED_CREATED_FILES (30, "MAX_FILES"),
    PARM_SPLIT_TAG ("|".getBytes(StandardCharsets.UTF_8)[0], "|"),
    PARM_START_TAG (">".getBytes(StandardCharsets.UTF_8)[0], ">"),
    PARM_END_TAG ("~".getBytes(StandardCharsets.UTF_8)[0], "~"),
    IMAGE_TYPE (0, "png"),
    ;

    public int parm1 = 0;
    public final String parm2;

    Pie_Constants(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    public int getParm1() {
        return parm1;
    }

    public String getParm2() {
        return parm2;
    }

}


