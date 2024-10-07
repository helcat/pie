package net.pie.command;

import net.pie.enums.*;
import net.pie.utils.*;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

public class Pie_Command_Map {
    private final Map<Pie_Word, Object> command_map = new HashMap<>();
    private Object error = null;
    private Pie_Run_Type runtype = null;

    /** ***************************************************************************<br>
     * Map from command line, Allows for Language translation, English, French Etc.
     */
    public Pie_Command_Map(String[] args, Pie_Run_Type runtype) {
        setRuntype(runtype);
        int count = 0;
        Pie_Word key = null;
        String value;

        List<Pie_Word> single_commands = Arrays.asList(Pie_Word.ENCODE, Pie_Word.DECODE, Pie_Word.HELP, Pie_Word.OVERWRITE, Pie_Word.MAKE_CERTIFICATE
                ,Pie_Word.VERIFY_CERTIFICATE, Pie_Word.CONSOLE, Pie_Word.BASE64_DECODE, Pie_Word.BASE64_ENCODE);
        List<Pie_Word> double_commands = Arrays.asList(Pie_Word.PREFIX, Pie_Word.FILE, Pie_Word.TEXT, Pie_Word.BASE64_FILE, Pie_Word.NAME, Pie_Word.CERTIFICATE,
                Pie_Word.ENCRYPTION, Pie_Word.OUTPUT, Pie_Word.SHAPE);
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

                value = args[count + 1].replace("\"", "");
                if (!value.startsWith("-"))
                    continue;

                for (Pie_Word key_value : double_commands) {
                    key = create_Key(key_value, arg.substring(1));
                    if (key != null) {
                        getCommand_map().put(key, value);
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

       // Has one of these Commands
        List<Pie_Word> has_One_Of_These = Arrays.asList(Pie_Word.ENCODE, Pie_Word.DECODE, Pie_Word.HELP, Pie_Word.MAKE_CERTIFICATE
                ,Pie_Word.VERIFY_CERTIFICATE, Pie_Word.BASE64_DECODE, Pie_Word.BASE64_ENCODE);
        boolean ok = false;
        for (Pie_Word p : has_One_Of_These) {
            ok = getCommand_map().containsKey(p);
            if (ok)
                break;
        }
        if (!ok) {
            setError(Pie_Word.FAILED);
            return;
        }

        // SHAPE Command
        if (!getCommand_map().containsKey(Pie_Word.SHAPE) || getCommand_map().get(Pie_Word.SHAPE) == null) {
            getCommand_map().put(Pie_Word.MODE, Pie_Shape.SHAPE_RECTANGLE);
        }else{
            check_Shape(getCommand_map().get(Pie_Word.SHAPE));
        }

        // MODE Command
        if (!getCommand_map().containsKey(Pie_Word.MODE) || getCommand_map().get(Pie_Word.MODE) == null) {
            getCommand_map().put(Pie_Word.MODE, Pie_Encode_Mode.M_2);
        }else{
            check_Mode(getCommand_map().get(Pie_Word.MODE));
        }

        // VERIFY CERTIFICATE Command
        if (getCommand_map().containsKey(Pie_Word.VERIFY_CERTIFICATE) && getCommand_map().get(Pie_Word.VERIFY_CERTIFICATE) == null) {
            setError(Pie_Word.NO_SOURCE);
            return;
        }

        // Max MB Command
        if (!getCommand_map().containsKey(Pie_Word.Max_MB))
            getCommand_map().put(Pie_Word.Max_MB, new Pie_Max_MB(500));
        else
            check_MaxMB(getCommand_map().get(Pie_Word.Max_MB));

        // LOG Command
        if (!getCommand_map().containsKey(Pie_Word.LOG) || getCommand_map().get(Pie_Word.LOG) == null) {
            getCommand_map().put(Pie_Word.LOG, Level.SEVERE);
        }else{
            check_Log(getCommand_map().get(Pie_Word.LOG));
        }

        // OUTPUT Command
        if (!getCommand_map().containsKey(Pie_Word.OUTPUT)) {
            setError(Pie_Word.OUTPUT_REQUIRED);
        }else{
            Pie_Output output = new Pie_Output(getCommand_map().get(Pie_Word.OUTPUT));
            if (output.getError() != null) {
                setError(output.getError());
                return;
            }
        }

        // Source
        check_Sources();
    }

    /** **************************************************<br>
     * Source file
     */
    private void check_Sources() {
        if (getCommand_map().containsKey(Pie_Word.FILE)) {
            try {
                if (getCommand_map().get(Pie_Word.FILE) != null && getCommand_map().get(Pie_Word.FILE) instanceof  File)
                    return;
                if (getCommand_map().get(Pie_Word.FILE) != null && getCommand_map().get(Pie_Word.FILE) instanceof  String) {
                    File source_file = new File((String) getCommand_map().get(Pie_Word.FILE));
                    if (!source_file.exists() || !source_file.isFile()) {
                        setError(Pie_Word.INVALID_FILE);
                        return;
                    }else {
                        getCommand_map().put(Pie_Word.FILE, source_file);
                    }
                }
            } catch (Exception ignored) {
                setError(Pie_Word.INVALID_FILE);
            }
        }

        else if (getCommand_map().containsKey(Pie_Word.TEXT)) {
            try {
                if (getCommand_map().get(Pie_Word.TEXT) == null || Pie_Utils.isEmpty((String) getCommand_map().get(Pie_Word.TEXT))) {
                    setError(Pie_Word.NO_SOURCE);
                    return;
                }
            } catch (Exception ignored) {  }
        }

        if (getCommand_map().containsKey(Pie_Word.BASE64_FILE)) {
            try {
                if (getCommand_map().get(Pie_Word.BASE64_FILE) != null && getCommand_map().get(Pie_Word.BASE64_FILE) instanceof  File) {
                    getCommand_map().put(Pie_Word.BASE64_FILE, new Pie_Base64((File) getCommand_map().get(Pie_Word.BASE64_FILE), Pie_Source_Type.BASE64_FILE));
                    return;
                } else if (getCommand_map().get(Pie_Word.BASE64_FILE) != null && getCommand_map().get(Pie_Word.BASE64_FILE) instanceof  String) {
                    File source_file = new File((String) getCommand_map().get(Pie_Word.BASE64_FILE));
                    if (!source_file.exists() || !source_file.isFile()) {
                        setError(Pie_Word.INVALID_FILE);
                        return;
                    }else {
                        getCommand_map().put(Pie_Word.BASE64_FILE, new Pie_Base64(source_file, Pie_Source_Type.BASE64_FILE));
                    }
                }
            } catch (Exception ignored) {
                setError(Pie_Word.INVALID_FILE);
            }
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
     * check Shape
     * @param mode Object
     */
    private void check_Shape(Object mode) {
        if (mode == null) {
            getCommand_map().put(Pie_Word.SHAPE, Pie_Shape.SHAPE_RECTANGLE);
            return;
        }

        if (mode instanceof String) {
            String m = (String) mode;
            if (Pie_Word.is_in_Translation(Pie_Word.RECTANGLE, m)) {
                getCommand_map().put(Pie_Word.SHAPE, Pie_Shape.SHAPE_RECTANGLE);
                return;
            }
            if (Pie_Word.is_in_Translation(Pie_Word.SQUARE, m)) {
                getCommand_map().put(Pie_Word.SHAPE, Pie_Shape.SHAPE_SQUARE);
                return;
            }
        }

        if (mode instanceof Pie_Shape) {
            getCommand_map().put(Pie_Word.SHAPE, mode);
            return;
        }

        getCommand_map().put(Pie_Word.SHAPE, Pie_Shape.SHAPE_RECTANGLE);
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

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Pie_Run_Type getRuntype() {
        return runtype;
    }

    public void setRuntype(Pie_Run_Type runtype) {
        this.runtype = runtype;
    }
}