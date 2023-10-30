package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Encryption_Type {
    AES ("AES","AES/CBC/PKCS5PADDING", "cbgf5ee0-a594-11", "egc9d5c0-t594-48"),
    ;

    public String type;
    public String cipher;
    public String encryptionKey;
    public String encryptioninitVector;

    Pie_Encryption_Type(String type, String cipher, String encryptionKey, String encryptioninitVector) {
        setType(type);
        setCipher(cipher);
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Encryption_Type get(int ordinal) {
        for (Pie_Encryption_Type s : Pie_Encryption_Type.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getEncryptioninitVector() {
        return encryptioninitVector;
    }

    public void setEncryptioninitVector(String encryptioninitVector) {
        this.encryptioninitVector = encryptioninitVector;
    }
}


