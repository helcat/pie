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
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "coreprint.png";

    public static File certificate = new
            File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"));
    public static File folder = Pie_Utils.getDesktop();

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {
        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
            //.add_Encode_Source(new Pie_Encode_Source(new Pie_Text("terry")))	// File to be encoded
            .add_Encode_Source(new File(
                Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) +  temp_To_Be_Encoded))
            .add_Directory(folder)   	                                    // Folder to place encoded file
            .add_Log_Level(Level.OFF)										// Optional logging level Default OFF
            .add_Max_MB(200)						                    // Optional largest file allowed before slicing Default is 200 MB
            .add_Mode(Pie_Encode_Mode.M_2)								    // Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
            .add_Encryption(new Pie_Base64("iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAACB0lEQVR42p2QfUvTARzEf5+GkT1okJWllWIj1CY/26xNw6+pZG6lk8hVpJvTjWbmQywVMWMlWWZlYCjKDF2ikhSGBEmPtlKwQqzI0Oo19ArqRy+h+++O47g7RUH5B35DQ6AVj2rGXeXF6DZgMnk5YDCxT83AYDZj0TSvUUVVKzGne1AspirS6n14JB2RVvY/0lMngtMTJmMiElOEELS30LHFzcy0gydfF3GN3yeh5htKdJ8VvQzzM3oYGRvAduwduQ1DxNXEIRHZBOPHmG0TrLMudo8Ir6OK+GjIYzQsKP2BRcrlGm9TDNTlvWK0t5KlWx/Y4D3J3hubuXB1gFm5gq82kourD3NwcoZ7Zw5xVmunJHcZefhygNK4bZxb/MVY+xTOdifTnY3ajA4sqTn4dF2c18yD5X6KYs34Ny6gT8pCkYV+MvVLTJ7OpM0tqKVeJFel07ZMcaEF2dqEhCZoSKxgTgu43R9Dc0sB771pKLV9CRwNfmIm3Mz81DpKuneQlVxGhaEEl4yTE9/O3R+bOHIzxLOeU6y8cJAiAYp3xaAEm6qZWzPIF/t3Wgt7iVqVzHWXjqQlP2WxeVS8SdXqOwiJDt/OHCILKhms9xPKt6NYE5fpDAhiC5IvIR50C/btGnfNk35nmdDICscvCdlrn3M5fj2P9zTydEioNmqHNZ1w8vmP8l/4Cx6ZoG2ZiL+YAAAAAElFTkSuQmCC") )		        // Optional Encryption. See Encryption Examples
            .add_Shape(Pie_Shape.SHAPE_SQUARE)								// Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
            .add_Option(Pie_Option.OVERWRITE_FILE)		                    // Optional set Pie_Option's as required. See Pie_Option examples
            .build();														// Build the Pie_Config

        Pie_Encode encode = new Pie_Encode(config);

        System.out.println(encode.getOutput_location());
    }
}