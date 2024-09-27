package net.pie.examples;

/*
 java -classpath pie-1.2.jar  net.pie.examples.Pie_Test_Encode

git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Shape;
import net.pie.utils.Pie_Base64;
import net.pie.utils.Pie_Utils;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Base64 {

    private String temp_To_Be_Encoded = "coreprint.png";
    public static File folder = Pie_Utils.getDesktop();

    public static void main(String[] args) {
        new Pie_Test_Base64(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Base64(String arg) {
        Pie_Base64 b64 = new Pie_Base64().encode_file(new File( Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) +  temp_To_Be_Encoded));
        System.out.println(b64.getText());
        b64.write_base64_to_File(new File( Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) +  temp_To_Be_Encoded + ".txt"));

        b64.decode_base64_write_to_File(new File( Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) +  temp_To_Be_Encoded + "new.png"));

    }
}