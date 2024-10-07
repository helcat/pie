import net.pie.command.Pie_Prompt;
import net.pie.enums.Pie_Run_Type;

/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * pixel.image.encode@gmail.com<br>
 *
 * This class allows for * java -cp .\pie-x.x.jar Pie<br>
 * Instead of java -cp .\pie-x.x.jar net.pie.command.Start which is the main class<br>
 */

public class Pie {

    public static void main(String[] args) {
        if (args != null && args.length > 0)
            new Pie_Prompt(args, Pie_Run_Type.COMMAND_LINE);
    }

}