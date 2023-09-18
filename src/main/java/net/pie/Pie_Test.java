package net.pie;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.enums.Pie_Position;
import net.pie.utils.Pie_Encoded_Destination;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

public class Pie_Test {
    /** ***********************************************<br>
     * <b>Main Start</b><br>
     * Runnable from jar. Example Arabic text "السلام عليكم هذا اختبار" Encoding
     * @param args if null will use the arabic text supplied.
     **/
    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
        BufferedImage image = encode(arg);
        if (image != null) {
            String value = decode(image);
            System.out.println(value);
        }
    }

    /** ******************************************************<br>
     * <b>encode</b><br>
     * Encodes text to the image.<br>
     * Create a basic config and source. Place the config in the source. Use source with the Pie_Encode function.
     * @param text
     * @return BufferedImage
     */
    public BufferedImage encode(String text) {
        Pie_Config config = new Pie_Config();
        config.setLog_level(Level.SEVERE);
        config.getMinimum().setDimension(0, 0, Pie_Position.MIDDLE_CENTER);

        Pie_Source source = new Pie_Source(config);
        //source.encode_Text(text == null ? "السلام عليكم هذا اختبار" : text);
        source.encode_Txt_File(config.getUtils().getDesktopPath() + File.separator + "Hello.txt");

        Pie_Encoded_Destination encoded_Image_destination = new Pie_Encoded_Destination();
        encoded_Image_destination.setLocal_file(new File(config.getUtils().getDesktopPath() + File.separator + "My_Image.png"));

        config.setSave_Encoded_Image(encoded_Image_destination);

        Pie_Encode encoder = new Pie_Encode(source);
        encoder.encode();
        BufferedImage image = encoder.getEncoded_image();

        return image;
    }

    /** ******************************************************<br>
     * <b>decode</b><br>
     * Decodes the image file to restore the text.<br>
     * Create a basic config. Place the config in the Pie_Decode along with the image file.
     * @param image
     * @return String
     */
    public String decode(BufferedImage image) {
        Pie_Config config = new Pie_Config();
        config.setLog_level(Level.SEVERE);

        Pie_Decode decoder = new Pie_Decode(config, image);
        decoder.decode();
        if (!decoder.getConfig().isError())
            return decoder.getDecoded_Message();
        return null;
    }
}