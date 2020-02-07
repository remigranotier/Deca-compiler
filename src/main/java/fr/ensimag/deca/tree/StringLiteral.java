package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * String literal
 *
 * @author gl01
 * @date 01/01/2020
 */
public class StringLiteral extends AbstractStringLiteral {
    private static final Logger LOG = Logger.getLogger(StringLiteral.class);
    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        //removing the double quotes from string value:
        value = value.substring(1, value.length() - 1);
        this.value = value;
    }

    /**
     * Implements terminal StringLiteral in pass 3
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify StringLiteral: start");
        Type result = new StringType(compiler.getSymbolTable().create("string"));
        setType(result);
        LOG.trace("verify StringLiteral: end");
        return result;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("\""+value+"\"");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }

}
