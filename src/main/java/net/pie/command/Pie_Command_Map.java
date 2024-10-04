package net.pie.command;

import net.pie.enums.*;
import net.pie.utils.*;

import java.util.*;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

public class Pie_Command_Map {
    private Map<Pie_Word, Object> command_map = new HashMap<>();
    private Pie_Word error = null;

    /** ***************************************************************************<br>
     * Map from command line
     */
    public Pie_Command_Map(String[] args) {
        int count = 0;
        String value;
        Pie_Word key = null;
        List<Pie_Word> single_commands = Arrays.asList(Pie_Word.ENCODE, Pie_Word.DECODE, Pie_Word.HELP, Pie_Word.OVERWRITE, Pie_Word.MAKE_CERTIFICATE
                ,Pie_Word.VERIFY_CERTIFICATE, Pie_Word.PROMPT, Pie_Word.CONSOLE, Pie_Word.BASE64_DECODE, Pie_Word.BASE64_ENCODE, Pie_Word.RECTANGLE, Pie_Word.SQUARE);
        List<Pie_Word> double_commands = Arrays.asList(Pie_Word.PREFIX, Pie_Word.FILE, Pie_Word.TEXT, Pie_Word.BASE64_FILE, Pie_Word.NAME, Pie_Word.CERTIFICATE,
                Pie_Word.ENCRYPTION, Pie_Word.OUTPUT);
        for (String arg : args) {
            if (arg.startsWith("-")) {
                for (Pie_Word key_value : single_commands) {
                    key = create_Key(key_value, arg.substring(1));
                    if (key != null)
                        break;
                }
                if (key != null) {
                    count ++;
                    continue;
                }

                for (Pie_Word key_value : double_commands) {
                    key = create_Key(key_value, arg.substring(1));
                    if (key != null) {
                        getCommand_map().put(key, args[count + 1].replace("\"", ""));
                        break;
                    }
                }
                if (key != null) {
                    count ++;
                    continue;
                }
            }
            count ++;
        }

        validate();
    }

    /** **************************************************<br>
     * create Key
     */
    private Pie_Word create_Key(Pie_Word key, String wording) {
        if (Pie_Word.is_in_Translation(key, wording)) {
            getCommand_map().put(key, null);
            return key;
        }
        return null;
    }

    /** **************************************************<br>
     * validate
     */
    private void validate() {
        List<Pie_Word> has_One_Of_These = Arrays.asList(Pie_Word.ENCODE, Pie_Word.DECODE, Pie_Word.HELP, Pie_Word.MAKE_CERTIFICATE
                ,Pie_Word.VERIFY_CERTIFICATE, Pie_Word.PROMPT, Pie_Word.BASE64_DECODE, Pie_Word.BASE64_ENCODE);
        boolean ok = false;
        for (Pie_Word p : has_One_Of_These) {
            ok = getCommand_map().containsKey(p);
            if (ok)
                break;
        }
        if (!ok) {
            setError(Pie_Word.ENCODING_FAILED);
            return;
        }

        ok = false;
        has_One_Of_These = Arrays.asList(Pie_Word.RECTANGLE, Pie_Word.SQUARE);
        for (Pie_Word p : has_One_Of_These) {
            ok = getCommand_map().containsKey(p);
            if (ok)
                break;
        }
        if (!ok) {
            getCommand_map().put(Pie_Word.RECTANGLE, null);
            return;
        }

        if (!getCommand_map().containsKey(Pie_Word.MODE) || getCommand_map().get(Pie_Word.MODE) == null) {
            getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_2);
        }else{
            check_Mode(getCommand_map().get(Pie_Word.MODE));
        }

        if (getCommand_map().containsKey(Pie_Word.VERIFY_CERTIFICATE) && getCommand_map().get(Pie_Word.VERIFY_CERTIFICATE) == null) {
            setError(Pie_Word.NO_SOURCE);
            return;
        }

        if (!getCommand_map().containsKey(Pie_Word.Max_MB))
            getCommand_map().put(Pie_Word.Max_MB, new Pie_Max_MB(500));
        else
            check_MaxMB(getCommand_map().get(Pie_Word.Max_MB));

        if (!getCommand_map().containsKey(Pie_Word.LOG) || getCommand_map().get(Pie_Word.LOG) == null) {
            getCommand_map().put(Pie_Word.LOG, Level.SEVERE);
        }else{
            check_Log(getCommand_map().get(Pie_Word.LOG));
        }
    }

    /** **************************************************<br>
     * check Prompt Log
     * @param log Object
     */
    private void check_Log(Object log) {
        if (log == null) {
            getCommand_map().put(Pie_Word.LOG, Level.SEVERE);
            return;
        }

        if (log instanceof String) {
            String m = (String) log;
            if (Pie_Word.is_in_Translation(Pie_Word.OFF, m)) {
                getCommand_map().put(Pie_Word.LOG, Level.OFF);
                return;
            }
            if (Pie_Word.is_in_Translation(Pie_Word.INFORMATION, m)) {
                getCommand_map().put(Pie_Word.LOG, Level.INFO);
                return;
            }
            if (Pie_Word.is_in_Translation(Pie_Word.SEVERE, m)) {
                getCommand_map().put(Pie_Word.LOG, Level.SEVERE);
                return;
            }
        }

        if (log instanceof Level) {
            getCommand_map().put(Pie_Word.LOG, log);
            return;
        }

        getCommand_map().put(Pie_Word.LOG, Level.SEVERE);
    }

    /** **************************************************<br>
     * check Mode
     * @param mode Object
     */
    private void check_Mode(Object mode) {
        if (mode == null) {
            getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_2);
            return;
        }

        if (mode instanceof String) {
            String m = (String) mode;
            if (Pie_Word.is_in_Translation(Pie_Word.ONE, m)) {
                getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_1);
                return;
            }
            if (Pie_Word.is_in_Translation(Pie_Word.TWO, m)) {
                getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_2);
                return;
            }
        }

        if (mode instanceof Pie_Encode_Mode) {
            getCommand_map().put(Pie_Word.MODE, mode);
            return;
        }

        getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_2);
    }

    /** **************************************************<br>
     * check Prompt MaxMB
     * @param maxmb Object
     */
    private void check_MaxMB(Object maxmb) {
        Pie_Max_MB m = new Pie_Max_MB(500);
        if (maxmb == null) {
            getCommand_map().put(Pie_Word.Max_MB, m);
            return;
        }

        if (maxmb instanceof String) {
            String v = ((String)  maxmb);
            m = new Pie_Max_MB(Pie_Utils.isEmpty(v.replaceAll("\\D", "")) ? Integer.parseInt (v.replaceAll("\\D", ""))  : 500 );
        }

        else if (maxmb instanceof Integer) {
            m = new Pie_Max_MB((Integer) maxmb);
        }

        if (m.getMb() < 10 || m.getMb() > 700)
            m = new Pie_Max_MB(700);
        getCommand_map().put(Pie_Word.Max_MB, m);
    }

    public Map<Pie_Word, Object> getCommand_map() {
        return command_map;
    }

    public Pie_Word getError() {
        return error;
    }

    public void setError(Pie_Word error) {
        this.error = error;
    }
}