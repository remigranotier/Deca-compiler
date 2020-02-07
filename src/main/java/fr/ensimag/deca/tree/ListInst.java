package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.log4j.Logger;

/**
 * List of instructions
 * @author gl01
 * @date 01/01/2020
 */
public class ListInst extends TreeList<AbstractInst> {
    private static final Logger LOG = Logger.getLogger(ListInst.class);
    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */    
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.trace("verify ListInst: start");
        for (AbstractInst inst : this.getList()) {
            inst.verifyInst(compiler, localEnv, currentClass, returnType);
        }
        LOG.trace("verify ListInst: end");
    }

    /**
     * Fonction a exécuter pour faire les instructions dans le cas ou l'on vient d'un else : permet de passer le label
     * en argument.
     * @param compiler
     * @param E
     */
    public void codeGenElse(DecacCompiler compiler, Label E) {
        for (AbstractInst i : getList()) {
            i.codeGenElse(compiler, E);
            if (i.isAssign() || i.mustNotGenCode()) {
                compiler.addToFirstRegisterNumber(-1);
            }
        }
    }

    /**
     * Generates the code for each of the instructions given
     * @param compiler
     */
    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
            if (i.mustNotGenCode() || i.isAssign() || i.isMethodCall()) {
                compiler.addToFirstRegisterNumber(-1);
                if (i.isMethodCall()) {
                    compiler.removeLastInstruction();
                }
            }
        }
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }
}
