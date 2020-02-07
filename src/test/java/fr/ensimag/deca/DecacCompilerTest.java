package fr.ensimag.deca;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.TypeDefinition;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.naming.InvalidNameException;

public class DecacCompilerTest {
    @Test
    public void destFilenameTest() {
        String correctName = "test.deca";
        String shortName = "test";
        String wrongExtension = "test.notgood";
        String correctNameOutput = "";
        try {
            correctNameOutput = DecacCompiler.destFilename(correctName);
            assert(correctNameOutput.equals("test.ass"));
        } catch (InvalidNameException e) {
            assert(false);
        }
        try {
            DecacCompiler.destFilename(shortName);
            assert(false);
        } catch (InvalidNameException e) {
            assert(true);
        }
        try {
            DecacCompiler.destFilename(wrongExtension);
            assert(false);
        } catch (InvalidNameException e) {
            assert(true);
        }
    }

    @Test
    public void initTest() {
        DecacCompiler compiler = new DecacCompiler(null, null);
        assertNotNull(compiler.getEnvTypes().get(compiler.getSymbolTable().create("int")));
        assertNotNull(compiler.getEnvTypes().get(compiler.getSymbolTable().create("void")));
        assertNotNull(compiler.getEnvTypes().get(compiler.getSymbolTable().create("float")));
        assertNotNull(compiler.getEnvTypes().get(compiler.getSymbolTable().create("boolean")));
        assertNotNull(compiler.getEnvTypes().get(compiler.getSymbolTable().create("Object")));
        ClassDefinition objectDef = (ClassDefinition) compiler.getEnvTypes().get(compiler.getSymbolTable().create("Object"));
        EnvironmentExp env = objectDef.getMembers();
        assertNotNull(env.get(compiler.getSymbolTable().create("equals")));
        assertEquals(1, objectDef.getNumberOfMethods());
        assertEquals(0, objectDef.getNumberOfFields());
    }
}