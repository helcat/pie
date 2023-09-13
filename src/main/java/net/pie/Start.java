package net.pie;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import java.io.File;

public class Start {
    /*************************************************
     * Main Start -> Runnable Jar السلام عليكم هذا اختبار
     *************************************************/
    public static void main(String[] args) {
        new Start("السلام عليكم هذا اختبار"); // 23
    }

    /*************************************************
     * Start -> From Jar Start
     *************************************************/
    public Object start(Object[] args) {
        main(new String[0]);
        return "plugin started";
    }

    /*************************************************
     * Start
     *************************************************/
    public Start(String toBeEncrypted) {
        Pie_Config config = new Pie_Config();
        config.setUse(Pie_Use.BLOCK3);
        Pie_Utils utils = new Pie_Utils(config);

        Pie_Encode encode = new Pie_Encode(toBeEncrypted);
        utils.saveImage_to_file(encode.getEncoded_image(), new File(utils.getDesktopPath() + File.separator + "myImage.png"));
        Pie_Decode decode = new Pie_Decode(encode.getEncoded_image());

        System.out.println(decode.getDecoded_Message());
    }

}