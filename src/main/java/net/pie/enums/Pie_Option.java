package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie Option</b><br>
 * Settings for encoding and Decoding.<br>
 * ENC_OVERWRITE_FILE : Allows created encoded files with the same name to be overwritten.<br>
 * RUN_GC_AFTER_PROCESSING : Run garbage collection after processing<br>
 * SHOW_MEMORY_USAGE : Show memory usage in logs when available<br>
 * SHOW_PROCESSING_TIME : Show the amount of time taken to process the file or Object, in logs<br
 * DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR : Will not delete the destination file if an error has occurred.<br>
 **/
public enum Pie_Option {
    ENC_OVERWRITE_FILE,
    RUN_GC_AFTER_PROCESSING,
    SHOW_MEMORY_USAGE,
    SHOW_PROCESSING_TIME,
    TERMINATE_LOG_AFTER_PROCESSING,
    DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR,
    MODULATION_OFF,
    DECODE_TEXT_TO_VARIABLE
    ;

}


