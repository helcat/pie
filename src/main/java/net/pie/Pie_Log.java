package net.pie;

import java.text.SimpleDateFormat;
import java.util.*;

public class Pie_Log {
    private enum Pie_LOG_TYPE {
        ERROR,
        WARNING,
        LOG
    }
    private SimpleDateFormat date_formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private boolean add_date = true;
    private Map<Pie_LOG_TYPE, List<String>> log = null;

    /** ***************************************************************<br>
     * <b>Pie_Log</b><br>
     * Set up Mapping for Errors, Warnings and Log<br>
     * Sets up add date - adds the date and time to each message.
     */
    public Pie_Log() {
        setAdd_date(true);
        setLog(new HashMap<Pie_LOG_TYPE, List<String>>());
        getLog().put(Pie_LOG_TYPE.ERROR, new ArrayList<String>());
        getLog().put(Pie_LOG_TYPE.WARNING, new ArrayList<String>());
        getLog().put(Pie_LOG_TYPE.LOG, new ArrayList<String>());
    }

    /** ***************************************************************<br>
     * <b>Add Error message</b><br>
     * Add a message to the error map and log
     */
    public void addError(String message) {
        getLog().get(Pie_LOG_TYPE.ERROR).add(getDate() + message);
        addLog(message);
    }
    /** ***************************************************************<br>
     * <b>Add Warning message</b><br>
     * Add a message to the warning map and log
     */
    public void addWarning(String message) {
        getLog().get(Pie_LOG_TYPE.WARNING).add(message);
        addLog(message);
    }
    /** ***************************************************************<br>
     * <b>Add Log message</b><br>
     * Add a message to the log map
     */
    public void addLog(String message) {
        getLog().get(Pie_LOG_TYPE.LOG).add(message);
    }

    /** ***************************************************************<br>
     * <b>Error Check</b><br>
     * Checks to see if an error has occurred
     */
    public boolean isError() {
        if (getLog().get(Pie_LOG_TYPE.ERROR).isEmpty())
            return false;
        return true;
    }
    /** ***************************************************************<br>
     * <b>Warning Check</b><br>
     * Checks to see if an warning has occurred
     */
    public boolean isWarning() {
        if (getLog().get(Pie_LOG_TYPE.WARNING).isEmpty())
            return false;
        return true;
    }
    /** ***************************************************************<br>
     * <b>Log Check</b><br>
     * Checks to see if a log entry has been created
     */
    public boolean isLog() {
        if (getLog().get(Pie_LOG_TYPE.LOG).isEmpty())
            return false;
        return true;
    }

    /** ***************************************************************<br>
     * <b>Display Errors</b><br>
     * Outputs the errors to the console.
     */
    public void display_Errors() {
        for (String s : getLog().get(Pie_LOG_TYPE.ERROR))
            System.out.println(s);
    }
    /** ***************************************************************<br>
     * <b>Display Warnings</b><br>
     * Outputs the warnings to the console.
     */
    public void display_Warnings() {
        for (String s : getLog().get(Pie_LOG_TYPE.WARNING))
            System.out.println(s);
    }
    /** ***************************************************************<br>
     * <b>Display Log</b><br>
     * Outputs the log entries to the console.
     */
    public void display_Log() {
        for (String s : getLog().get(Pie_LOG_TYPE.LOG))
            System.out.println(s);
    }

    /** ***************************************************************<br>
     * <b>Date</b><br>
     * Create a date for the messages
     */
    private String getDate() {
        if (isAdd_date()) {
            try {
                return getDate_formatter().format(new java.util.Date()) + " : ";
            } catch (Exception e) {  }
        }
        return "";
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public Map<Pie_LOG_TYPE, List<String>> getLog() {
        return log;
    }

    private void setLog(Map<Pie_LOG_TYPE, List<String>> log) {
        this.log = log;
    }

    private boolean isAdd_date() {
        return add_date;
    }

    public void setAdd_date(boolean add_date) {
        this.add_date = add_date;
    }

    public SimpleDateFormat getDate_formatter() {
        return date_formatter;
    }

    public void setDate_formatter(SimpleDateFormat date_formatter) {
        this.date_formatter = date_formatter;
    }
}


