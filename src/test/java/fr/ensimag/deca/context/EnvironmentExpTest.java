package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnvironmentExpTest {
    @Test
    public void testEnvironmentExp() {
        DecacCompiler compiler = new DecacCompiler(null, null);
        EnvironmentExp env = new EnvironmentExp(null);
        SymbolTable.Symbol bonsoir = compiler.getSymbolTable().create("bonsoir");
        ExpDefinition myDef = new VariableDefinition(null, Location.BUILTIN);
        try {
            env.declare(bonsoir, myDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            assert(false);
        }
        assertTrue(env.association.containsKey(bonsoir));
        assertNotEquals(env.get(bonsoir), null);
        assertEquals(env.get(bonsoir), myDef);

        EnvironmentExp sonEnv = new EnvironmentExp(env);
        SymbolTable.Symbol bonjour = compiler.getSymbolTable().create("bonjour");
        ExpDefinition myDef2 = new VariableDefinition(null, Location.BUILTIN);
        try {
            sonEnv.declare(bonjour, myDef2);
        } catch (EnvironmentExp.DoubleDefException e) {
            assert(false);
        }
        assertTrue(sonEnv.association.containsKey(bonjour));
        assertEquals(sonEnv.get(bonsoir), myDef);
        assertEquals(sonEnv.get(bonjour), myDef2);

        SymbolTable.Symbol bonjour2 = compiler.getSymbolTable().create("bonjour");
        ExpDefinition myDef3 = new VariableDefinition(null, Location.BUILTIN);
        try {
            sonEnv.declare(bonjour, myDef3);
        } catch (EnvironmentExp.DoubleDefException e) {
            return;
        }
        assert(false);
    }
}