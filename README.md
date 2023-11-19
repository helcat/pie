# PIE (Pixel Image Encoding)
PIE is a file encoding and encryption library for java 8+ (encryption is optional). PIE's purpose is to encode a file or plain text in to an image, 
then decode back again when required. When encoding the file, its size should remain the same, however this depends on the options used to when encoding.

The PIE library is lightweight and does not have any dependencies. This should make it easier to integrate into almost any application. 
To make sure there is no confusion and for compatibility all methods in the library are prefixed with `"Pie_"`. Although written in Java libraries for 
other languages will be available in the future.

#### Image modulation
In order to stop security applications and systems from detecting what the encoding is generating. Like calculating a hash or interrogating the image. 
Every image produced has modulation. Which means Every image is completely different. No matter how large or small the image is or how many parts there are. 
This is not a part of the encryption, it's a stand able feature to protect the image generation.

#### Practical applications 
Would include but not limited to 
* Chat servers (Passing encoded images (text to image) from one client to another).
* Hiding files or text in plain sight.
* Storing images or other files on a cloud server. 
* Passing information across to any server, network or computer system to bypass security.
* Stop AI models from using your images or other files for training.

When the file or text is encoded the end result is just an image.
Any inspection of the file will pass as an image and unless the security denies images will pass through. When the file is decoded the file or text will be
intact. Even the metadata of the file is restored. Any file can be encoded for example Images, Movies, Documents, already compressed files including zip and 
even Exe or Application files.

## Encoding - Getting started
Create a configuration file first. This will store all the defaults and options for the encoding. A lot of the options have already been defaulted but can be 
overridden.

`Pie_Config encoding_config = new Pie_Config();`

Next you can add options to the configuration. 
These "options" are not required and can be used for encoding and decoding, defaults will be used if not entered into the configuration.
You do not have to place the "options" or any parameters in any order, enter them as needed.
* `Pie_Option.ENC_OVERWRITE_FILE` : will overwrite any already encoded file. Default false.
* `Pie_Option.SHOW_PROCESSING_TIME` : will show how long the process took. Default false.
* `Pie_Option.RUN_GC_AFTER_PROCESSING` : will run garbage collection after processing. Default false.
* `Pie_Option.SHOW_MEMORY_USAGE` : shows approximate memory usage. Default false.
* `Pie_Option.TERMINATE_LOG_AFTER_PROCESSING` : will terminate the logging. Default false.

Example

`new Pie_Config(Pie_Option.ENC_OVERWRITE_FILE, Pie_Option.SHOW_PROCESSING_TIME);`

### Other options with more detail.
#### Pie_Shape : *** Encoding only. (Optional) Changes the shape of the image. Values allowed are :
* `SHAPE_RECTANGLE` : default.
* `SHAPE_SQUARE`

#### Pie_Encode_Mode : *** Encoding only. (Optional) Changes the type of encoding to be used. Values allowed are :
* `ENCODE_MODE_R` Image will be red - X Large file size<br>
* `ENCODE_MODE_G` Image will be green - X Large file size<br>
* `ENCODE_MODE_B` Image will be blue - X Large file size<br>
* `ENCODE_MODE_RT` use red channel only Fully transparent - X Large file size<br>
* `ENCODE_MODE_GT` use green channel only Fully transparent - X Large file size<br>
* `ENCODE_MODE_BT` use blue channel only Fully transparent - X Large file size<br>
* `ENCODE_MODE_GB` Image will be green and blue - Large file size<br>
* `ENCODE_MODE_RB` Image will be red and blue - Large file size<br>
* `ENCODE_MODE_RG` Image will be red and green - Large file size<br>
* `ENCODE_MODE_GBT` use green and blue channels only Fully transparent - Large file size<br>
* `ENCODE_MODE_RBT` use red and blue channels only Fully transparent - Large file size<br>
* `ENCODE_MODE_RGT` use red and green channels only Fully transparent - Large file size<br>
* `ENCODE_MODE_RGB` Image will be red, green and blue - Medium file size <br>
* `ENCODE_MODE_RGBT` use red, green and blue channels Fully transparent - Medium file size <br>
* `ENCODE_MODE_ARGB` full encoding. Small File size. (Default Setting)

#### Pie_ZIP_Name : *** Encoding only. (Optional) sets a custom name format
* `AS_IS` Default. Leaves the name alone, however if multiple files are needed, a number is added.
* `RANDOM` generates a random name to hide the real file name when decoding.
* `NUMBER` issues an incremental number as a file name.

#### Pie_ZIP_Option : *** Encoding only. (Optional) sets when a zip file should be used.
* `NEVER` a zip file is never used. Any files will be created as is.
* `ALWAYS` a zip file is always used.
* `ONLY_WHEN_EXTRA_FILES_REQUIRED` Default. Only creates a zip file when needed.

#### Level : *** Encoding and Decoding. (Optional) This is a standard java logging parameter .
* `Level.SEVERE` is the default.

### Utilities *** Encoding and Decoding.
These methods are available for ease of use and are not required for encoding or decoding.
* `Pie_Utils.getDesktop()` gets the desktop as a file object.
* `Pie_Utils.getDesktopPath()` gets the desktop as a string path.

### Object options.

#### Pie_Encode_Source *** Encoding only. (Optional) This is a required parameter
* `new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "My_Document.doc"))`

Add a source file to the configuration. This is the file you want encoded.

#### Pie_Encode_Max_MB *** Encoding only. (Optional)
You can set the Maximum MB for the encoding. If the file exceeds this, the file will be split. Default is 200mb.
* `new Pie_Encode_Max_MB(200)`
Important note. Pie_Encode_Max_MB is just one method of protecting the library. There is a "MAX_PROTECTED_SIZE" which cannot be changed. 
Is this an image height and width the encoded image can not exceed. This is hardcoded to 15000 x 15000. This limitation will be removed in version 2 of the library
in the future.

#### Pie_Encryption *** Optional Encoding and Decoding.
* `new Pie_Encryption("My Pass Phrase Goes Here");`
* `new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "certificate_name"))`

If encryption on encoding used, then you must add this to the decoding process. if the encoding does not have encryption and this parameter is used, the decryption process is ignored.
To add encryption, enter one of the above parameters into the configuration. Neither the pass phrase nor the certificate is encoded in the file created.
You will need a key to decrypt the encoded file. if you lose your key the file will remain locked. The pass phrase can be a string value, or you can 
create a certificate file to hold the encryption key. A certificate should make it easier to decode and share the files with others. Files with 
encryption might be a little larger than the original but not much (only a few bytes longer). It is highly recommended to use a USB drive to store the certificate.

To create a certificate use this standalone command.

        Pie_Encryption encryption = new Pie_Encryption("123456789 £ ABCDEFGHIJ abcdefghij -> More");
        encryption.create_Certificate_File(new Pie_Config(Level.INFO), Pie_Utils.getDesktop(), "pie_certificate");

First create a "Pie_Encryption" object with a pass phrase. The Phrase must be longer than 7 characters. Next create the certificate using the "create_Certificate_File" method.
Parameters for the method are as follows, please note you can add them in any order.
* `Pie_Config` Optional. If not entered a default configuration will be used.
* `File` Optional. A folder to store the certificate. If not entered the desktop will be used.
* `String` Optional. A file name for the certificate. If not entered "pie_certificate" will be used. An extension is not required ".pie" is added to the file name.

#### Pie_Encoded_Destination *** Optional 
* `new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + "my_encoded_file"))`

Add a destination to the configuration. This is where you want the encoded file to be stored. Optional, you can give the new file a name. "png" or "zip" will be added
when required so an extension is not required.

#### Pie_Encode_Min_Size *** Optional
* `new Pie_Encode_Min_Size(100, 100, Pie_Position.MIDDLE_CENTER)`

If the encoded image is tiny, and you want the end file to be a certain size, you can add a minimum size parameter to the configuration with an offset positional layout.
The default position is "Pie_Position.MIDDLE_CENTER" however you can use any of the following.

* `TOP_LEFT`
* `TOP_CENTER`
* `TOP_RIGHT`
* `MIDDLE_LEFT`
* `MIDDLE_CENTER` Default.
* `MIDDLE_RIGHT`
* `BOTTOM_LEFT`
* `BOTTOM_CENTER`
* `BOTTOM_RIGHT`

### Encoding example
        Pie_Config encoding_config = new Pie_Config(
                new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate")),
                new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "my_movie.avi")),
                new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + "i_have_been_encoded")),
        );
        new Pie_Encode(encoding_config);

## Decoding - Getting started
Create a configuration file first. This will store all the defaults and options for the decoding process. A lot of the options have already been defaulted but can be
overridden.
