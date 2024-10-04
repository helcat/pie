package net.pie.command;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.pie.enums.Pie_Word;

import java.nio.charset.StandardCharsets;

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

public class Start implements RequestHandler<Object, byte[]> {

    public static void main(String[] args) {
        if (args == null || args.length == 0)
            System.exit(0);
        new Pie_Prompt(new Pie_Command_Map(args).getCommand_map());
    }

    /**
     * @Override public String handleRequest(Object input, Context context) {

     * }
     **/

    @Override
    public byte[] handleRequest(Object input, Context context) {
        byte[] imageBytes = null;
        try {
            if (input != null) {
                String myinput = input.toString();
                //Pie_Mapped mapped = new Pie_Mapped(((HashMap<String, Object>) input));
                imageBytes = myinput.getBytes(StandardCharsets.UTF_8);

            }
        }catch (Exception ignored) {  }
        return imageBytes;
    }
}