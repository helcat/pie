package net.pie;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import java.io.File;

public class Pie_Test {
    /*************************************************
     * Main Start -> Runnable Jar السلام عليكم هذا اختبار
     *************************************************/
    public static void main(String[] args) {
        new Pie_Test();
    }

    /*************************************************
     * Start
     *************************************************/
    public Pie_Test() {
        Pie_Config config = new Pie_Config();
        config.getMinimum().setDimension(0, 0, Pie_Position.MIDDLE_CENTER);

        Pie_Source source = new Pie_Source();
        source.encrypt_Text("السلام عليكم هذا اختبار");

        Pie_Encode encode = new Pie_Encode(config, source);
        config.getUtils().saveImage_to_file(encode.getEncoded_image(), new File(config.getUtils().getDesktopPath() + File.separator + "myImage.png"));
        Pie_Decode decode = new Pie_Decode(config, encode.getEncoded_image());

        System.out.println(decode.getDecoded_Message());
    }

}