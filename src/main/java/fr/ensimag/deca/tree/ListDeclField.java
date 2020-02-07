package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * List of field declaration
 * @author gl01
 * @date 20/01/2020
 */
public class ListDeclField extends TreeList<DeclField>{

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        for (DeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non terminal list_decl_field of pass 2
     * @param compiler contains env_types
     * @param superclass
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyListDeclFieldPass2(DecacCompiler compiler, AbstractIdentifier superclass, AbstractIdentifier currentClass) throws ContextualError {
        for (int i = 0; i < superclass.getClassDefinition().getNumberOfFields(); i++) {
            currentClass.getClassDefinition().incNumberOfFields();
        }

        for (DeclField decl : getList()) {
            boolean isRedefinition = decl.verifyDeclFieldPass2(compiler, superclass, currentClass);
            if (!isRedefinition){
                currentClass.getClassDefinition().incNumberOfFields();
            }
        }
    }

    /**
     * Implements non terminal list_decl_field of pass 3
     * @param compiler contains env_types
     * @param envExp
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyListDeclFieldPass3(
            DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass) throws ContextualError {
        for (DeclField decl : getList()) {
            decl.verifyDeclFieldPass3(compiler, envExp, currentClass);
        }
    }

    /**
     * Generates the code for the initialization of the object in the case it inherits from Object
     * @param compiler
     */
    public void codeGenListDeclFieldBasic(DecacCompiler compiler) {
        compiler.addComment("initialization of own fields");
        for (DeclField c : getList()) {
            c.codeGenDeclField(compiler);
        }
    }

    /**
     * Generates the code for the initialization of the object in the case it inherits from another class
     * @param compiler
     * @param superclass
     */
    public void codeGenListDeclFieldInherited(DecacCompiler compiler, AbstractIdentifier superclass) {
             compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), R1));

             for (DeclField c : getList()) {
                c.codeGenDeclFieldZero(compiler);
            }

            compiler.addComment("Call for initialization of inherited fields");
            compiler.addInstruction(new PUSH(Register.R1));
            compiler.incrementCurrentNumberOfRegisterPushed();
            compiler.addInstruction(new BSR(new Label("init." + superclass.getName().toString())));  // TODO : Check le paramètre :
            compiler.addToCurrentNumberOfRegisterPushed(2);
            compiler.addToCurrentNumberOfRegisterPushed(-2);

            compiler.addInstruction(new SUBSP(1), "We get the stack back to its initial state");  // TODO : arg de subsp à verifier ?
            compiler.decrementCurrentNumberOfRegisterPushed();
    }
}
