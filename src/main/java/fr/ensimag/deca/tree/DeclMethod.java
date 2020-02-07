package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Declaration of a class' method
 * @author gl01
 * @date 20/01/2020
 */
public class DeclMethod extends Tree{
    private final AbstractIdentifier type;


    private final AbstractIdentifier methodName;
    private final ListDeclParam listParams;
    private final AbstractMethodBody body;

    public AbstractIdentifier getMethodName() {
        return methodName;
    }

    private static final Logger LOG = Logger.getLogger(DeclMethod.class);
    public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName, ListDeclParam listParams, AbstractMethodBody body) {
        this.type = type;
        this.methodName = methodName;
        this.listParams = listParams;
        this.body = body;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        listParams.decompile(s);
        s.print(")");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        listParams.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        listParams.iter(f);
        body.iter(f);
    }

    /**
     * Implements decl_method of pass 2
     * @param compiler contains env_types
     * @param superclass
     * @param currentClass
     * @return boolean stating if the current field declaration is a redefinition or not (for indexes)
     * @throws ContextualError
     */
    protected boolean verifyDeclMethodPass2(DecacCompiler compiler,
                                         AbstractIdentifier superclass,
                                         AbstractIdentifier currentClass) throws ContextualError {
        LOG.debug("verify decl method " + methodName.getName() + " pass 2: start");
        boolean isRedefinition = false;

        int index = currentClass.getClassDefinition().getNumberOfMethods();
        Type typeObtained = type.verifyType(compiler);
        Signature sig = listParams.verifyListDeclParamPass2(compiler);
        EnvironmentExp superEnv = superclass.getClassDefinition().getMembers();
        Definition def = superEnv.get(methodName.getName());
        if (def != null) {
            MethodDefinition potentialMethodDef =
                    def.asMethodDefinition(methodName.getName() + " is not declared as " +
                            "method in current context (2.7)", getLocation());
            Signature sig2 = potentialMethodDef.getSignature();
            Type type2 = potentialMethodDef.getType();
            if (!sig.equals(sig2) || !compiler.getEnvTypes().subType(typeObtained, type2)) {
                throw new ContextualError("Bad method " + methodName.getName() + " redefinition " +
                        "(incorrect signature or wrong type) (2.7)", getLocation());
            }
            index = potentialMethodDef.getIndex();
            isRedefinition = true;
        }

        try {
            MethodDefinition newDef = new MethodDefinition(typeObtained, getLocation(), sig, index);
            methodName.setDefinition(newDef);
            methodName.setType(typeObtained);
            currentClass.getClassDefinition().getMembers().declare(methodName.getName(), newDef);
            LOG.debug("verify decl method " + methodName.getName() + " pass 2: end");
            return isRedefinition;
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("method " + methodName.getName() +
                    " is already declared in class " + currentClass.getName() + " (2.7)", getLocation());
        }
    }

    /**
     * Implements decl_method of pass 3
     * @param compiler cpnv_types
     * @param envExp class environment
     * @param currentClass
     * @throws ContextualError
     */
    protected void verifyDeclMethodPass3(DecacCompiler compiler,
                                         EnvironmentExp envExp,
                                         AbstractIdentifier currentClass) throws ContextualError {
        LOG.debug("verify decl method " + methodName.getName() + " pass 3: start");
        Type returnType = type.verifyType(compiler);
        EnvironmentExp envParams = listParams.verifyListDeclParamPass3(compiler, envExp);
        body.verifyMethodBody(compiler, envExp, envParams, currentClass, returnType);
        LOG.debug("verify decl method " + methodName.getName() + " pass 3: end");
    }


    /**
     * Generates the code of the method
     * @param compiler
     * @param className
     */
    public void codeGenMethod(DecacCompiler compiler, AbstractIdentifier className) {
        IMAProgram methodCode = new IMAProgram(); //New code block : useful to add things at the beginning of the method
        compiler.setMethodCode(methodCode);

        // beginning of method mode
        compiler.changeMode();

        // reset counters used for the TSTO instruction, so we are sure they are initialized correctly
        compiler.resetNbVar();
        compiler.resetRegisterCount();
        compiler.resetFirstRegisterNumber();

        listParams.setOperands();

        //generating the method body
        body.codeGenMethod(compiler, className, methodName);

        //label of the beginning of the method code
        methodCode.addFirstLabel(new Label("code." + className.getName().toString() + "." +
                methodName.getName().toString()));


        //end of method mode
        compiler.appendProgram(methodCode);
        compiler.changeMode();
    }

}
