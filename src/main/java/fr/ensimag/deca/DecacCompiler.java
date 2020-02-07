package fr.ensimag.deca;

import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

import javax.naming.InvalidNameException;
import java.io.*;
import java.util.HashMap;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl01
 * @date 01/01/2020
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Number of the first register to be used (Rn in documentation)
     */
    private int firstRegisterNumber = 2;

    public void setGBOffset(int GBOffset) {
        this.GBOffset = GBOffset;
    }

    public int getGBOffset() {
        return GBOffset;
    }

    /**
     * GB offset to use to store global variables at different
     */
    private int GBOffset = 1;


    /**
     * Max number of register pushed : usefull to test stack overflow : we know how many registers we will push on the
     * stack.
     */
    private int maxNumberOfRegisterPushed = 0;

    public int getMaxNumberOfRegisterPushed() {
        return maxNumberOfRegisterPushed;
    }

    /**
     * Current number of register pushed, used to get the its max number for the TSTO instruction
     */
    private int currentNumberOfRegisterPushed = 0;


    /**
     * Adds 1 to the number of current registers pushed ; updates its max number
     */
    public void incrementCurrentNumberOfRegisterPushed(){
        currentNumberOfRegisterPushed++;
        if (currentNumberOfRegisterPushed > maxNumberOfRegisterPushed) {
            maxNumberOfRegisterPushed = currentNumberOfRegisterPushed;
        }
    }

    /**
     * Subtracts 1 to the number of current registers pushed ; updates its max number
     */
    public void decrementCurrentNumberOfRegisterPushed(){
        currentNumberOfRegisterPushed--;
    }

    /**
     * Adds n to the number of current registers pushed ; updates its max number.
     * n can be negative.
     * @param n
     */
    public void addToCurrentNumberOfRegisterPushed(int n) {
        currentNumberOfRegisterPushed += n;
        if (currentNumberOfRegisterPushed > maxNumberOfRegisterPushed) {
            maxNumberOfRegisterPushed = currentNumberOfRegisterPushed;
        }
    }

    /**
     * resets the register counts : useful to ensure the counter is properly initialized upon entering a new block.
     */
    public void resetRegisterCount() {
        currentNumberOfRegisterPushed = 0;
        maxNumberOfRegisterPushed = 0;
    }

    /**
     * Size of the method table : needed to generate the initial SUBSP instruction, as we store it on the stack.
     */
    private int methodTableSize;

    /**
     * Sets the method table size to n.
     * @param n
     */
    public void setMethodTableSize(int n) {
        methodTableSize = n;
    }

    public int getMethodTableSize() {
        return methodTableSize;
    };


    /**
     * Computes the initial SP offset to do with ADDSP, from the different counters present in this class.
     */
    public void genInitialSPOffset() {
        addFirstInstruction(new ADDSP(methodTableSize + nbVar));
        addFirstComment("ADDSP : method table size : " + methodTableSize + ", nb of var " + nbVar);
    }



    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        if (compilerOptions != null) {
            this.compilerOptions = compilerOptions;
        } else {
            this.compilerOptions = new CompilerOptions();
        }
        this.source = source;
        try {
            getEnvTypes().declare(getSymbolTable().create("void"),
                    new TypeDefinition(new VoidType(getSymbolTable().create("void")), Location.BUILTIN));
            getEnvTypes().declare(getSymbolTable().create("boolean"),
                    new TypeDefinition(new BooleanType(getSymbolTable().create("boolean")), Location.BUILTIN));
            getEnvTypes().declare(getSymbolTable().create("float"),
                    new TypeDefinition(new FloatType(getSymbolTable().create("float")), Location.BUILTIN));
            getEnvTypes().declare(getSymbolTable().create("int"),
                    new TypeDefinition(new IntType(getSymbolTable().create("int")), Location.BUILTIN));

            ClassType objectClass = new ClassType(getSymbolTable().create("Object"), Location.BUILTIN, null);
            ClassDefinition objectDef = objectClass.getDefinition();

            //Set the addr and the method table of object
            objectDef.setMethodTableAddr(new RegisterOffset(1, Register.GB));
            HashMap<Integer, LabelOperand> objectLabelTable = new HashMap<Integer, LabelOperand>();
            objectLabelTable.put(0, Label.getMethodLabel("Object", "equals"));
            objectDef.setLabelTable(objectLabelTable);
            objectDef.setNumberOfFields(0);
            objectDef.setNumberOfMethods(0);

            getEnvTypes().declare(getSymbolTable().create("Object"), objectDef);
            EnvironmentExp envExpObject = objectDef.getMembers();
            SymbolTable.Symbol equals = getSymbolTable().create("equals");
            Signature sigEquals = new Signature();
            sigEquals.add(objectClass);
            envExpObject.declare(equals,
                new MethodDefinition(getEnvTypes().get(getSymbolTable().create("boolean")).getType(),
                        Location.BUILTIN, sigEquals, 0));
            objectDef.incNumberOfMethods();

        } catch (EnvironmentType.DoubleDefException e) {
            throw new DecacInternalError("Double builtin types definition (should not appear)");
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new DecacInternalError("Error while creating Object Class (should not appear)");
        }
        errorManager = new ErrorManager(this);
    }

    /**
     * Error manager of the compiler.
     */
    private ErrorManager errorManager;

    public ErrorManager getErrorManager() {
        return this.errorManager;
    }


    /**
     * Get the number of the first free register to be used
     * @return firstRegisterNumber
     */
    public int getFirstRegisterNumber() {
        return firstRegisterNumber;
    }


    /**
     * Resets the register managment. Useful to ensure all counters are properly initialized upon entering a new block.
     */
    public void resetFirstRegisterNumber() {
        firstRegisterNumber = 2;
        maxRegisterUsed = 2;
    }

    /**
     * Add k to the number of the first register to be used (can be negative)
     * @param k
     * @throws IllegalArgumentException : if the result number is below 2.
     */
    public void addToFirstRegisterNumber(int k) {
        if (firstRegisterNumber + k < 2) {
            throw new IllegalArgumentException("Register managment error : trying to add " + k + " to the number " +
                    "of the first usable register when this number is " + firstRegisterNumber + ", resulting in an" +
                    " invalid register number.");
        }
        firstRegisterNumber += k;
        if (firstRegisterNumber > maxRegisterUsed) {
            maxRegisterUsed = firstRegisterNumber;
        }
    }

    /**
     * number of the max value of firstRegisterNumber : used to store all used registers in a method before entering it.
     */
    private int maxRegisterUsed = 0;

    public int getMaxRegisterUsed() {
        return maxRegisterUsed;
    }

    public void resetMaxRegisterUsed() {
        maxRegisterUsed = 0;
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * Environment of types (dictionary)
     */
    public EnvironmentType getEnvTypes() {
        return envTypes;
    }

    /**
     *  Symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * Number of variables in program
     */
    public int getNbVar() {
        return nbVar;
    }

    public void resetNbVar() {
        nbVar = 0;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        if (methodMode) {
            methodCode.add(line);
        } else {
            program.add(line);
        }
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        if (methodMode) {
            methodCode.addComment(comment);
        } else {
            program.addComment(comment);
        }
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        if (methodMode) {
            methodCode.addLabel(label);
        } else {
            program.addLabel(label);
        }
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        if (methodMode) {
            methodCode.addInstruction(instruction);
        } else {
            program.addInstruction(instruction);
        }
    }


    /**
     * remove the last instruction : useful as codeGenInst on a method call stores the result of the function in R2,
     * but if it is the last instruction of a line, this last LOAD instruction is useless : we get rid of it
     */
    public void removeLastInstruction() {
        if (methodMode) {
            methodCode.removeLastInstruction();
        } else {
            program.removeLastInstruction();
        }
    }


    /**
     * Add a new instruction at the beginning of the program
     * @param instruction
     */
    public void addFirstInstruction(Instruction instruction) {
        if (methodMode) {
            methodCode.addFirst(instruction);
        } else {
            program.addFirst(instruction);
        }
    }

    /**
     * Add a new instruction and a commentary at the beginning of the program
     * @param instruction
     * @param comment
     */
    public void addFirstInstruction(Instruction instruction, String comment) {
        if (methodMode) {
            methodCode.addFirst(instruction, comment);
        } else {
            program.addFirst(instruction, comment);
        }
    }

    /**
     * Adds a comment at the beginning of the program
     * @param comment
     */
    public void addFirstComment(String comment) {
        if (methodMode) {
            methodCode.addFirst(new Line(comment));
        } else {
            program.addFirst(new Line(comment));
        }
    }

    /**
     * Adds a line at the beginning of the program, uncommented
     * @param line
     */
    public void addLine(String line) {
        if (methodMode) {
            methodCode.add(new AsmLine(line));
        } else {
            program.add(new AsmLine(line));
        }
    }


    /**
     * Method mode : when switched on, all additions to the program will be done to a sub-program instead, that will
     * then be appended at the end of the main program.
     * it is used for method : as they are separate blocks, this method allows us to add instruction at the beginning
     * of the block, like the TSTO instruction.
     */
    private boolean methodMode = false;

    /**
     * Swaps the current mode.
     */
    public void changeMode() {
        methodMode = !methodMode;
    }

    /**
     * Code of the method sub-program
     */
    private IMAProgram methodCode;

    public void setMethodCode(IMAProgram prog) {
        methodCode = prog;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        if (methodMode) {
            methodCode.addInstruction(instruction, comment);
        }else {
            program.addInstruction(instruction, comment);
        }
    }
    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }
    private final CompilerOptions compilerOptions;

    private final File source;

    public void setNbVar(int nbVar) {
        this.nbVar = nbVar;
    }

    /**
     * Number of variables in the block : used for the TSTO instruction, as they are stored on the stack.
     */
    private int nbVar;

    private EnvironmentType envTypes = new EnvironmentType();

    private SymbolTable symbolTable = new SymbolTable();

    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile;
        try {
            destFile = destFilename(sourceFile);
        } catch (InvalidNameException e) {
            LOG.fatal("Wrong extension name (not .deca)");
            System.err.println("Error : source file extension is not .deca");
            return true;
        }

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert(prog.checkAllLocations());
        if (compilerOptions.getParseOnly()) {
            prog.decompile(System.out);
            return false;
        }


        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());

        if (compilerOptions.getVerificationOnly()) {
            return false;
        }

        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     * Throws LocationException when a compilation error (incorrect program)
     * occurs.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

    /**
     * Small function to generate assembly file name from the source name.
     *
     * @param sourceFile Name of the source file. Must be "*.deca".
     * @return Name of the dest file (.ass)
     *
     * @throws InvalidNameException When the source file name is not "*.deca".
     */
    public static String destFilename(String sourceFile) throws InvalidNameException {
        String[] detailedName = sourceFile.split("\\.");
        if (detailedName.length <= 1 || !detailedName[detailedName.length - 1].equals("deca")) {
            throw new InvalidNameException();
        }
        detailedName[detailedName.length - 1] = "ass";
        return String.join(".", detailedName);
    }


    /**
     * Append the given program to the end of the main program. Used at the end of a method mode.
     * @param methodCode
     */
    public void appendProgram(IMAProgram methodCode) {
        program.append(methodCode);
    }

    /**
     * Label of the end of the current method : used so that we can access it easily in a method for the particular
     *  case of 'return' instruction, that needs it.
     */
    Label endOfCurrentMethod;

    public Label getEndOfCurrentMethod() {
        return endOfCurrentMethod;
    }

    public void setEndOfCurrentMethod(Label endOfCurrentMethod) {
        this.endOfCurrentMethod = endOfCurrentMethod;
    }

    /**
     * Returns if the compiler is currently in method mode.
     * @return
     */
    public boolean isInMethodMode() {
        return methodMode;
    }
}
