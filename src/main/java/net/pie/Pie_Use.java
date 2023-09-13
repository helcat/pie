package net.pie;

import java.nio.charset.StandardCharsets;

public enum Pie_Use {
    BLOCK3 (3),
    BLOCK4 (4);

    private int number ;

    Pie_Use(int number) {
        setNumber(number);
    }

    /*************************************
     * getters and setters
     *************************************/
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}


