# PIE (Pixel Image Encoding)
PIE is a file encoding and encryption library for java 8+ (encryption is optional). PIE's purpose is to encode a file or plain text in to an image, then decode back again.
When encoding the file, its size should remain the same, however this depends on the options used to when encoding the file.

The PIE library does not have any dependencies and should integrate into almost any application. 
Due to not having any dependencies, wrappers can be created for other programming languages. 
Although libraries for other languages will be available in the future.

Practical applications would include but not limited to chat servers, hiding files or text in plain sight, storing images or files on a cloud server and 
passing information across to any server, network or computer system to bypass security. When the file or text is encoded the end result is just an image.
Any inspection of the file will pass as an image and unless the security denies images will pass through. When the file is decoded the file or text will be
intact. Even the metadata of the file is restored. Any file can be encoded for example Images, Movies, Documents, already compressed files including zip and 
even Exe or Application files.

## Encoding - Getting started
Create a configuration file first. This will store all the defaults and options for the encoding. A lot of the options have already been defaulted but can be 
overridden.

`Pie_Config encoding_config = new Pie_Config();`

Next you can add options to the configuration. 
These "options" are not required, defaults will be used if not entered into the configuration.
You do not have to place the "options" or any parameters in any order, enter them as needed.
* `Pie_Option.ENC_OVERWRITE_FILE` : will overwrite any already encoded file. Default false.
* `Pie_Option.SHOW_PROCESSING_TIME` : will show how long the process took. Default false.
* `Pie_Option.RUN_GC_AFTER_PROCESSING` : will run garbage collection after processing. Default false.
* `Pie_Option.SHOW_MEMORY_USAGE` : shows approximate memory usage. Default false.
* `Pie_Option.TERMINATE_LOG_AFTER_PROCESSING` : will terminate the logging. Default false.

Example

`new Pie_Config(Pie_Option.ENC_OVERWRITE_FILE, Pie_Option.SHOW_PROCESSING_TIME);`

### Other options with more detail.
#### Pie_Shape : Changes the shape of the image. Values allowed are :
* `SHAPE_RECTANGLE` : default.
* `SHAPE_SQUARE`

#### Pie_Encode_Mode : Changes the type of encoding to be used. Values allowed are :
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

#### Pie_ZIP_Name : sets a custom name format
* `AS_IS` Default. Leaves the name alone, however if multiple files are needed, a number is added.
* `RANDOM` generates a random name to hide the real file name when decoding.
* `NUMBER` issues an incremental number as a file name.

#### Pie_ZIP_Option : sets when a zip file should be used.
* `NEVER` a zip file is never used. Any files will be created as is.
* `ALWAYS` a zip file is always used.
* `ONLY_WHEN_EXTRA_FILES_REQUIRED` Default. Only creates a zip file when needed.

#### Level : This is a standard java logging parameter .
* `Level.SEVERE` is the default.

### Object options.
#### Pie_Encryption *** Optional
* `new Pie_Encryption("My Pass Phrase Goes Here");`
* `new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "certificate_name"))`

To add encryption, enter one of the above as a parameter into the configuration. Note either the pass phrase or certificate is encoded in the file created.
you will need these keys to decrypt the encoded file. if you lose your keys the file will remain locked. The pass phrase can be a string value, or you can 
create a certificate file to hold the encryption key. A certificate should make it easier to decode the files later on. Files with encryption might be a little 
larger than the original but not much. It is highly recommended to use a USB drive to store and save the certificate.

#### Pie_Encode_Source *** This is a required parameter
* `new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + "My_Document.doc"))`

Add a source file to the configuration. This is the file you want encoded.

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