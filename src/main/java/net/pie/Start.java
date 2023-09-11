package net.pie;

public class Start {

    // Runnable Jar
    public static void main(String[] args) {
        new Start();
    }

    // From Jar Start
    public Object start(Object[] args) {
        main(new String[0]);
        return "plugin started";
    }

    public Start() {

    }
}