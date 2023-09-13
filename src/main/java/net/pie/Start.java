package net.pie;

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

        Pie_Encode encode = new Pie_Encode(toBeEncrypted);

        Pie_Decode decode = new Pie_Decode(encode.getEncoded_image());

        System.out.println(decode.getDecoded_Message());
    }

}