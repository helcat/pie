# <span style='color:CornflowerBlue'>PIE (Pixel Image Encoding)</span>

### <span style='color:CornflowerBlue'>Content</span>
* [Encoding - Getting started](#encoding---getting-started)


* [Encoding Options](#encoding-options)
  * [Pie Encode Source](#pie-encode-source)
    * [Pie Text](#pie-text)
  * [Pie Encode Mode](#pie-encode-mode)
  * [Pie Encode Max MB](#pie-encode-max-mb)
  * [Pie Encode Min Size](#pie-encode-min-size)
  * [Pie Encoded Destination](#pie-encoded-destination)
  * [Pie Shape](#pie-shape)
  * [Pie ZIP Name](#pie-zip-name)
  * [Pie ZIP Option](#pie-zip-option)
  * [Encoding Example](#encoding-example)


* [Encoding and Decoding Options](#encoding-and-decoding-options)
  * [Level (Java Logging)](#level)
  * [Pie Encryption](#pie-encryption)
  * [Pie URL](#Pie-URL)
  * [Utilities](#utilities)
  * [Useful Encoding methods for after processing](#useful-encoding-methods-for-after-processing)
    * [isEncoding_Error](#isencoding-error)
    * [getEncoded_file_list](#getencoded-file-list)


* [Decoding - Getting started](#decoding---getting-started)
  * [Decoding Pie Options](#Decoding-Pie-Options)
  * [Pie Decode Source Pie](#pie-decode-source)
  * [Pie Decode Destination](#pie-decode-destination)
  * [Decoding Example](#decoding-example)


* [Useful Decoding Methods For After Processing](#useful-decoding-methods-for-after-processing)
  * [getDecoded file path](#getdecoded-file-path)
  * [isDecoding Error](#isdecoding-error)
  * [getDecoding_Error_Message](#getDecoding-Error-Message)

PIE is a file encoding and encryption library for java 8+ (encryption is optional). PIE's purpose is to encode a file or plain text in to an image, 
then decode back again when required. When encoding the file, its size should remain the same however, depends on the options used to when encoding.

The PIE library is lightweight and has no dependencies. This should make it easy to integrate into almost any application. 
To make sure there is no compatibility issues all classes in the library are prefixed with `"Pie_"`.

Every image produced has modulation by default however, this can be turned off. Which means Every image is completely different even if it's the same content being encoded. 

Practical applications Would include but not limited to 
* Chat servers . Ideally to pass a text encoded image from one client to another.
  * Process :  Text "Hello World" --> encode --> send to client --> decode --> view. Any one observing will only see an image nothing else.
* Hiding files or text in plain sight.
  * Process : Document --> encode --> store for later. Any sensitive files can be stored. If stolen or hacked the files will be locked.
* Storing images or other files on a cloud server. 
  * Process : Image --> encode --> Upload. Store any encoded file online and never worry about intrusions even if someone gets your password.
* Passing information across to any server, network or computer system.
  * Process : Sensitive Text / File / Command --> encode / encrypt --> send. Any one observing will only see an image nothing else.
* and many more....

When the file or text is encoded the end result is just a plain "png" image.
Any inspection of the file will pass as an image and unless the security denies images will pass through. When the file is decoded the file or text will be
intact. Even the metadata of the file is restored. Any file can be encoded for example but not limited to Images, Movies, Documents, Zips, Exe, Application 
and even already encrypted files.

Known Limitations
* Can be a little slow depending on the device used to encode and or encrypt large files over 600mb.
* Splitting files, each file is split to 200mb. Can be overridden using "Pie_Encode_Max_MB" but make sure you have enough memory.
* Splitting files, the number of files that can be produced is 30. Which is about 6gb.
* When using urls to download files for encoding only. if the "getContentLengthLong" is not available the file will not be downloaded. In this case download the file yourself and 
 use the inputstream and size method. Decoding does not need to know the size of a file.

These limitations can be fixed by adding dependencies and if you have the source code.
 
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
* `Pie_Option.DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR` : will override the default of deleting the file if an error has occurred.
* `Pie_Option.MODULATION_OFF` : will turn off modulation. Not recommended, only turn off if necessary.

Example

`new Pie_Config(Pie_Option.ENC_OVERWRITE_FILE, Pie_Option.SHOW_PROCESSING_TIME);`

## Encoding Options
Add these to "`new Pie_Config(....)`"

### Pie Encode Source
Encoding only. **This is a required parameter**
* `new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "My_Document.doc"))`
* `new Pie_Encode_Source(new Pie_URL("https..../myfile.jpg"))`
* `new Pie_Encode_Source(new URL("https..../myfile.jpg"))`
* `new Pie_Encode_Source( *** HttpURLConnection or HttpsURLConnection))` : Use your own connection to download the file.
* `new Pie_Encode_Source(new Pie_Text("This is some text to encode"))` Encode text using "Pie_Text". ** Pie_Text can also handle a file.

When using an online source, make sure the file name is included either as a header or on the end of the url.
Add a source file to the configuration. This is the file you want encoded.

#### Pie Text
Encoding only. (Optional). Can only be used with "Pie_Encode_Source"

* `new Pie_Encode_Source(new Pie_Text("Привет, это тест"))` Using plain text.
* `new Pie_Encode_Source(new Pie_Text("مرحبا هذا اختبار للفطيرة", "myfile.txt"))` Using plain text and assigning a file name.
* `new Pie_Encode_Source(new Pie_Text(new File(...."my_text_file.txt")))` Using a text file and using the text file name.
* `new Pie_Encode_Source(new Pie_Text(new File(...."my_text_file.txt"), "myfile.png"))` Using a text file and assigning a file name.

If using a text file, the file Must end with ".txt". The text inside will be encoded directly, Arabic and Cyrillic can also be used.
* Option 1, Text to encode. A file name of "Text.txt" will be used.
* Option 2, Text to encode and you can assign a file name.
* Option 3, Use a text file. The file will not be embedded but the name will be assigned.
* Option 4, Use a text file and you can assign a file name.

File names are used in the encoded image, Just in case the end client wants to export the text to a file. However, the end result can be a direct to variable output.

### Pie Encode Mode
Encoding only. (Optional) Changes the type of encoding to be used. Values allowed are :</span>
* `Pie_Encode_Mode.ENCODE_MODE_R` Image will be red - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_G` Image will be green - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_B` Image will be blue - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RT` use red channel only Fully transparent - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_GT` use green channel only Fully transparent - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_BT` use blue channel only Fully transparent - X Large file size
* `Pie_Encode_Mode.ENCODE_MODE_GB` Image will be green and blue - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RB` Image will be red and blue - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RG` Image will be red and green - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_GBT` use green and blue channels only Fully transparent - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RBT` use red and blue channels only Fully transparent - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RGT` use red and green channels only Fully transparent - Large file size
* `Pie_Encode_Mode.ENCODE_MODE_RGB` Image will be red, green and blue - Medium file size
* `Pie_Encode_Mode.ENCODE_MODE_RGBT` use red, green and blue channels Fully transparent - Medium file size
* `Pie_Encode_Mode.ENCODE_MODE_ARGB` full encoding. Small File size. Default.

### Pie Encode Max MB
Encoding only. (Optional)
You can set the Maximum MB for the encoding. If the file exceeds this, the file will be split to this value. Default is 200mb.
* `new Pie_Encode_Max_MB(200)`

Important. Pie_Encode_Max_MB is just one method of protecting the library. There is a "MAX_PROTECTED_SIZE" which cannot be changed.
Is this an image height and width the encoded image can not exceed. This is hardcoded to 15000 x 15000. This limitation will be removed in version 2 of the library
in the future.

### Pie Encode Min Size
Optional
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

### Pie Encoded Destination
Optional
* `new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + "my_encoded_file"))`
* `new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath()))`

Add a destination to the configuration. Enter a folder, this is where you want the encoded file to be stored. You can give the new file a name, but if one is not entered the
name of the file to encoded will be used. "png" or "zip" will be added so an extension is not required.

### Pie Shape
Encoding only. (Optional) Changes the shape of the image. Values allowed are :
* `Pie_Shape.SHAPE_RECTANGLE` : Default.
* `Pie_Shape.SHAPE_SQUARE`

### Pie ZIP Name
Encoding only. (Optional) sets a custom name format
* `Pie_ZIP_Name.AS_IS` Leaves the name alone, however if multiple files are needed, a number is added. Default.
* `Pie_ZIP_Name.RANDOM` generates a random name to hide the real file name when decoding.
* `Pie_ZIP_Name.NUMBER` issues an incremental number as a file name.

### Pie ZIP Option
Encoding only. (Optional) sets when a zip file should be used.
* `Pie_ZIP_Option.NEVER` a zip file is never used. Any files will be created as is.
* `Pie_ZIP_Option.ALWAYS` a zip file is always used.
* `Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED` Only creates a zip file when needed. Default.

### Encoding example

        Pie_Config encoding_config = new Pie_Config(
                new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate")),
                new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "my_movie.avi")),
                new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + "i_have_been_encoded")),
        );
        new Pie_Encode(encoding_config);

## Encoding and Decoding Options

### Level
(Optional) This is a standard java logging parameter. Add to "`new Pie_Config(Level.SEVERE)`"
* `Level.SEVERE` is the default.

### Pie Encryption
Optional Encoding and Decoding.
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

### Pie URL
Optional Encoding and Decoding.
* `new Pie_Encode_Source(new Pie_URL("https://www.mydomain.com/my_to_be_encoded_file.exe"))`
* `new Pie_Decode_Source(new Pie_URL("https://www.mydomain.com/my_encoded_file.png"))`

"Pie_URL" is a helpful class which allows a file to be downloaded without handling any errors. Can be used with "Pie_Encode_Source" or "Pie_Decode_Source" 

### Utilities
These methods are available for ease of use and are not required for encoding or decoding.
* `Pie_Utils.getDesktop()` gets the desktop as a file object.
* `Pie_Utils.getDesktopPath()` gets the desktop as a string path.
* `Pie_Utils.getTempFolder()` gets the temporary folder as a file object. Also handles the OSX Path.
* `Pie_Utils.getTempFolderPath()` gets the temporary folder as a string path. Also handles the OSX Path.

### Useful Encoding methods for after processing
#### isEncoding Error
Encoding only. (Optional)
Will return true or false to indicate if the encoding process failed.

        Pie_Encode encode = new Pie_Encode(encoding_config);
        System.out.println(encode.isEncoding_Error());

#### getEncoded file list
Encoding only. (Optional)
Will return the path to the encoded files, so you can use this in your own source.

        Pie_Encode encode = new Pie_Encode(encoding_config);
        encode.getEncoded_file_list().forEach(System.out::println);

## Decoding - Getting started
Create a configuration file first. This will store all the defaults and options for the decoding process. A lot of the options have already been defaulted but can be
overridden. If the encoded file has encryption and "Pie_Encryption" is not set an error will occur. To Decrypt an encoding supply the "Pie_Encryption" option with the 
correct information.

### Decoding Pie Options
* `Pie_Option.DECODE_TEXT_TO_VARIABLE` : If the encoded file contains text only. This option will set the text to output. See Decoding to variable.

### Pie Decode Source
Decoding only. Required parameter
* `new Pie_Decode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "My_Encoded_Image.png"))` : Using a file. 
* `new Pie_Decode_Source(new URL("https://www.mydomain.com/my_encoded_file.png"))` : Use a URL. However, you will have to handle the exception: java.net.MalformedURLException.
* `new Pie_Decode_Source(new Pie_URL("https://www.mydomain.com/my_encoded_file.png"))` : Use a URL with Pie_URL. you do not have to handle the exception. See Pie_URL for more details.
* `new Pie_Decode_Source( ** InputStream ** )` : Using an InputStream, FileInputStream, ByteArrayInputStream, url.openStream() etc.

Add a source file to where the encoded file was stored to.

### Pie Decode Destination
Decoding only. (Optional). Desktop will be used if no destination entered.
* `new Pie_Decode_Destination(new File(Pie_Utils.getDesktop()))` : Using a file object. Directory only, the file name is embedded in the encoded file.

Add a destination for the decoded source.

### Decoding Example
        Pie_Config decoding_config = new Pie_Config(
                new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate")),
                new Pie_Decode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + my_encoded_file.png")),
                new Pie_Decode_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + "my_folder")),
        );
        new Pie_Decode(decoding_config);

## Useful Decoding Methods For After Processing

### getDecoded file path
Decoding only. (Optional)
Will return the path to the decoded file, so you can use this in your own source. By default, if an error has occurred the destination file will be deleted and 
getDecoded_file_path will return null. You can override this by using `Pie_Option.DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR`.

        Pie_Decode decoded = new Pie_Decode(decoding_config);
        System.out.println(decoded.getDecoded_file_path());

### isDecoding Error
Decoding only. (Optional)
Will return true or false to indicate if the decoding process failed. A failure is only considered if "Level.SEVERE" is issued from the decoding process.

        Pie_Decode decoded = new Pie_Decode(decoding_config);
        System.out.println(decoded.isDecoding_Error());

### getDecoding Error Message
Decoding only. (Optional)
Will return the last error message occurred, but only if "isDecoding_Error" is true. 

        Pie_Decode decoded = new Pie_Decode(decoding_config);
        if (decoded.isDecoding_Error())
            System.out.println(decoded.getDecoding_Error_Message());
          

