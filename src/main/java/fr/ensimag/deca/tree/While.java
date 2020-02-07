package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * While statement
 * @author gl01
 * @date 01/01/2020
 */
public class While extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(While.class);
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        Label cond = Label.getNewWhileCond();
        Label beginning = Label.getNewWhileBeginning();

        compiler.addComment("Beginning of a new while loop");
        compiler.addInstruction(new BRA(cond));

        compiler.addLabel(beginning);
        body.codeGenListInst(compiler);

        compiler.addLabel(cond);
        condition.controlFlow(compiler, true, beginning);
        compiler.addComment("End of a while loop");

    }

    /**
     * Implements terminal While of pass 3
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
        LOG.trace("verify While: start");
        condition.verifyCondition(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, returnType);
        LOG.trace("verify While: end");
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
