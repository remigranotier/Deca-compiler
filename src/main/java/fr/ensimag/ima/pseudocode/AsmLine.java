package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;

public class AsmLine extends AbstractLine{
    private String value;

    public AsmLine(String value) {
        this.value = value;
    }

    @Override
    void display(PrintStream s) {
        s.println(value);
    }
}
