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
    private boolean add_date = false;
    private Map<Pie_LOG_TYPE, List<String>> log = null;

    public Pie_Log() {
        setAdd_date(true);
        setLog(new HashMap<Pie_LOG_TYPE, List<String>>());
        getLog().put(Pie_LOG_TYPE.ERROR, new ArrayList<String>());
        getLog().put(Pie_LOG_TYPE.WARNING, new ArrayList<String>());
        getLog().put(Pie_LOG_TYPE.LOG, new ArrayList<String>());
    }

    /*************************************
     * Add
     *************************************/
    public void addError(String message) {
        getLog().get(Pie_LOG_TYPE.ERROR).add(getDate() + message);
        addLog(message);
    }
    public void addWarning(String message) {
        getLog().get(Pie_LOG_TYPE.WARNING).add(message);
        addLog(message);
    }
    public void addLog(String message) {
        getLog().get(Pie_LOG_TYPE.LOG).add(message);
    }

    /*************************************
     * is
     *************************************/
    public boolean isError() {
        if (getLog().get(Pie_LOG_TYPE.ERROR).isEmpty())
            return false;
        return true;
    }
    public boolean isWarning() {
        if (getLog().get(Pie_LOG_TYPE.WARNING).isEmpty())
            return false;
        return true;
    }
    public boolean isLog() {
        if (getLog().get(Pie_LOG_TYPE.LOG).isEmpty())
            return false;
        return true;
    }

    /*************************************
     * Display
     *************************************/
    public void display_Errors() {
        for (String s : getLog().get(Pie_LOG_TYPE.ERROR))
            System.out.println(s);
    }
    public void display_Warnings() {
        for (String s : getLog().get(Pie_LOG_TYPE.WARNING))
            System.out.println(s);
    }
    public void display_Log() {
        for (String s : getLog().get(Pie_LOG_TYPE.LOG))
            System.out.println(s);
    }

    /** ***************************
     * Date
     ******************************/
    private String getDate() {
        if (isAdd_date()) {
            try {
                return getDate_formatter().format(new java.util.Date()) + " : ";
            } catch (Exception e) {  }
        }
        return "";
    }

    /*************************************
     * getters and setters
     *************************************/
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


