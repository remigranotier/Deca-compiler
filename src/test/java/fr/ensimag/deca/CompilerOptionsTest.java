package fr.ensimag.deca;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompilerOptionsTest {

    @Test
    public void parseArgsVide() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertTrue(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsBseul() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-b"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertTrue(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertTrue(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsMultipleD() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-d", "-d", "-d", "-d", "-d", "src/test/deca/syntax/valid/vide.deca"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(3, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertFalse(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsParallelAndNoCheck() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-P", "-n", "src/test/deca/syntax/valid/vide.deca"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertTrue(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertTrue(options.getNoCheck());
        assertFalse(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsParse() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-p", "src/test/deca/syntax/valid/vide.deca"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertTrue(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertFalse(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsVerif() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-v", "src/test/deca/syntax/valid/vide.deca"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(15, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertTrue(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertFalse(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsRegister5() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-r", "5", "src/test/deca/syntax/valid/vide.deca"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(false);
        }
        assertEquals(0, options.getDebug());
        assertEquals(4, options.getMaxRegister());
        assertFalse(options.getParallel());
        assertFalse(options.getPrintBanner());
        assertFalse(options.getVerificationOnly());
        assertFalse(options.getParseOnly());
        assertFalse(options.getNoCheck());
        assertFalse(options.getSourceFiles().isEmpty());
    }

    @Test
    public void parseArgsTooLongArg() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-bb"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Invalid argument"));
        }
    }

    @Test
    public void parseArgsUnknownOption() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-x"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Unknown option"));
        }
    }

    @Test
    public void parseArgsPV() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-p", "-v"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Cannot use -v and -p at the same time"));
        }
    }

    @Test
    public void parseArgsVP() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-v", "-p"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Cannot use -v and -p at the same time"));
        }
    }

    @Test
    public void parseArgsDoubleRegister() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-r", "5", "-r", "6"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Cannot have multiple"));
        }
    }

    @Test
    public void parseArgsNotIntegerToken() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-r", "-d"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Expecting integer"));
        }
    }

    @Test
    public void parseArgsNotConvenientRegisters() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-r", "2"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Max register"));
        }
    }

    @Test
    public void parseArgsNotExistingFile() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"coucou"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("does not exist"));
        }
    }

    @Test
    public void parseArgsBnonSeul() {
        CompilerOptions options = new CompilerOptions();
        String[] args = {"-b", "-n"};
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            assert(e.getMessage().contains("Option -b must be used without an"));
        }
    }
}