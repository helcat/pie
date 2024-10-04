package net.pie.command;

import net.pie.enums.Pie_Word;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com<br>
 *<br>
 * This class allows for * java -cp .\pie-1.3.jar Pie<br>
 * Instead of java -cp .\pie-1.3.jar net.pie.command.Start which is the main class<br>
 *
 * Convert to C
 * Use Graavml
 * .\native-image.cmd --shared -jar ..\..\pie-1.3.jar -o ..\..\..\pie
 */

public class Start_Old {

    public static void main(String[] args) {
        if (args == null || args.length == 0)
            System.exit(0);
        new Pie_Prompt(new Pie_Command_Map(args).getCommand_map());
    }

}