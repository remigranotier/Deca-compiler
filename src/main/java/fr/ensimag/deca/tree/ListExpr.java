package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl01
 * @date 01/01/2020
 */
public class ListExpr extends TreeList<AbstractExpr> {


    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;
        for (AbstractExpr exp : getList()) {
            if (first) {
                exp.decompile(s);
                first = false;
            } else {
                s.print(", ");
                exp.decompile(s);
            }
        }
    }
}
