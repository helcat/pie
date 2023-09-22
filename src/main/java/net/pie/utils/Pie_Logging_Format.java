package net.pie.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Pie_Logging_Format extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getLevel() + ": " + record.getMessage() + "\n";
    }
}


