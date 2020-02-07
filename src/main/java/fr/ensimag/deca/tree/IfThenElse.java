package fr.ensimag.deca.tree;

import com.sun.tools.internal.xjc.generator.util.LazyBlockReference;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Full if/else if/else statement.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class IfThenElse extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(IfThenElse.class);
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    /**
     * Sets the 'else' branch of 'this'
     * @param elseBranch
     */
    public void setElseBranch(ListInst elseBranch){
        this.elseBranch = elseBranch;
    }

    /**
     * Gets the 'else' branch of 'this'
     * @return
     */
    public ListInst getElseBranch(){
        return this.elseBranch;
    }

    /**
     * Implements terminal IfThenElse of pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to the "return" attribute (void in the main bloc).
     * @throws ContextualError
     */
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.trace("verify IfThenElse: start");
        condition.verifyCondition(compiler, localEnv, currentClass);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        LOG.trace("verify IfThenElse: end");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("Beginning of a new If statement");
        Label endIf = Label.getNewEndIf();

        codeGenElse(compiler, endIf);

        compiler.addLabel(endIf);

    }

    @Override
    protected void codeGenElse(DecacCompiler compiler, Label end){
        Label elseIf = Label.getNewElse();
        condition.controlFlow(compiler, false, elseIf);
        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(end));

        compiler.addLabel(elseIf);
        elseBranch.codeGenElse(compiler, end);

    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.print(") {");
        s.println();
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("}");
        if (elseBranch.getList().isEmpty()) {
            return;
        }
        s.print(" else {");
        s.println();
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
