package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceOfTest {
    @Test
    public void testInit() {
        DecacCompiler compiler = new DecacCompiler(null, null);
        AbstractExpr left = new Plus(new IntLiteral(4), new IntLiteral(5));
        AbstractExpr right = new Plus(new IntLiteral(4), new IntLiteral(5));
        InstanceOf bool = new InstanceOf(left, right);
        try {
            bool.verifyExpr(compiler, null, null);
        } catch (ContextualError e) {
            assert(e.getMessage().contains("Right operand of instanceof"));
            System.out.println(e.getMessage());
            return;
        }
        assert(false);
    }
}