package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Base</b><br>
 * Uses a layer of Base64 or Base85 but this option is not encoded to the file.<br>
 * The encoder and the decoder must be set to this option. The Default is Base64.<br>
 * <b>Pie_Author Note</b><br>
 * Base85 class was added, but with conflicting results.<br>
 * Using Base85 encoding, uses less pixels in the image and is faster, however the file size is larger.<br>
 * Using Base64 encoding, uses more pixels and a little slower (not much), but the file size is smaller.<br>
 * Was going to leave out but decided to leave that up to the end user.<br>
 * Unlike all the rest of the options, this option must be set on both encoder and the decoder config files.<br>
 **/
public enum Pie_Base {
    BASE64,
    BASE85,
    ;
}


