package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.util.Objects;

/**
 * Representation of a label in IMA code. The same structure is used for label
 * declaration (e.g. foo: instruction) or use (e.g. BRA foo).
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class Label extends Operand {


    @Override
    public String toString() {
        return name;
    }

    public Label(String name) {
        super();
        Validate.isTrue(name.length() <= 1024, "Label name too long, not supported by IMA");
        Validate.isTrue(name.matches("^[a-zA-Z][a-zA-Z0-9_.]*$"), "Invalid label name " + name);
        this.name = name;
    }
    private String name;

    private static int newBoolExpr = 0;

    public static Label getNewEndOfBoolExpr() {
        Label newLabel = new Label("EndOfBoolExpr" + newBoolExpr);
        newBoolExpr++;
        return newLabel;
    }

    private static int newControlFlow = 0;

    public static Label getNewControlFlow() {
        Label newLabel = new Label("ControlFlowBlock" + newControlFlow);
        newControlFlow++;
        return newLabel;
    }

    private static int newWhileCond;

    public static Label getNewWhileCond() {
        Label newLabel = new Label("WhileCondition" + newWhileCond);
        newWhileCond++;
        return newLabel;
    }

    private static int newWhileBeginning;

    public static Label getNewWhileBeginning() {
        Label newLabel = new Label("WhileBeginning" + newWhileBeginning);
        newWhileBeginning++;
        return newLabel;
    }

    private static int newElse;

    public static Label getNewElse() {
        Label newLabel = new Label("Else" + newElse);
        newElse++;
        return newLabel;
    }


    private static int newEndIf;

    public static Label getNewEndIf() {
        Label newLabel = new Label("EndIf" + newEndIf);
        newEndIf++;
        return newLabel;
    }

    private static int newCastSuccess;

    public static Label getNewCastSuccess() {
        Label newLabel = new Label("CastSuccess" + newCastSuccess);
        newCastSuccess++;
        return newLabel;
    }

    public static LabelOperand getMethodLabel(SymbolTable.Symbol className, SymbolTable.Symbol methodName) {
        return new LabelOperand (new Label("Code." + className + "." + methodName));
    }

    public static LabelOperand getMethodLabel(SymbolTable.Symbol className, String methodName) {
        return new LabelOperand (new Label("Code." + className + "." + methodName));
    }

    public static LabelOperand getMethodLabel(String className, String methodName) {
        return new LabelOperand (new Label("Code." + className + "." + methodName));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
