package fr.ensimag.deca;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class CompilerOptions {
    // Debug levels
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    // Getters
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getVerificationOnly() {
        return verificationOnly;
    }

    public boolean getParseOnly() {
        return parseOnly;
    }

    public boolean getNoCheck() {
        return noCheck;
    }

    public int getMaxRegister() {
        return maxRegister;
    }

    // Attributes initializations
    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private List<File> sourceFiles = new ArrayList<File>();
    private boolean verificationOnly = false;
    private boolean parseOnly = false;
    private boolean noCheck = false;
    private int maxRegister = 15;
    private boolean registerAlreadySet = false;

    /**
     * Verify that the options entered by the user do follow the specifications of the usage.
     * @param args List of String arguments already parsed by java
     *             Example : "decac -v -p"; args = ["-v", "-p"]
     * @throws CLIException When the user options do not follow the documented usage
     */
    public void parseArgs(String[] args) throws CLIException {
        for (int i = 0; i < args.length; i++) { // Not a foreach because we need the index
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                if (arg.length() > 2) {
                    throw new CLIException("Invalid argument \""+arg+"\"");
                }
                if (arg.charAt(1) == 'b') {
                    if (args.length != 1) {
                        throw new CLIException("Option -b must be used without any" +
                                " other options and without source file");
                    }
                    printBanner = true;
                } else if (arg.charAt(1) == 'P') {
                    parallel = true;
                } else if (arg.charAt(1) == 'd') {
                    debug++;
                    if (debug > 3) {
                        debug = 3;
                    }
                } else if (arg.charAt(1) == 'v') {
                    if (!parseOnly) {
                        verificationOnly = true;
                    } else {
                        throw new CLIException("Cannot use -v and -p at the same time");
                    }
                } else if (arg.charAt(1) == 'p') {
                    if (!verificationOnly) {
                        parseOnly = true;
                    } else {
                        throw new CLIException("Cannot use -v and -p at the same time");
                    }
                } else if (arg.charAt(1) == 'n') {
                    noCheck = true;
                } else if (arg.charAt(1) == 'r') {
                    if (registerAlreadySet) {
                        throw new CLIException("Cannot have multiple -r options");
                    }
                    i++;
                    if (i >= args.length || !args[i].matches("[0-9]+")) {
                        throw new CLIException("Expecting integer token after '-r'");
                    }
                    int number = Integer.parseInt(args[i]);
                    if (number < 4 || number > 16) {
                        throw new CLIException("Max register number must be in [| 4; 16 |]");
                    }
                    maxRegister = number - 1;
                    registerAlreadySet = true;
                } else {
                    throw new CLIException("Unknown option " + arg);
                }
            } else {
                // we consider that we're parsing a file name
                File toBeAdded = new File(arg);
                if (!toBeAdded.exists()) {
                    throw new CLIException("File " + toBeAdded + " does not exist");
                }
                if (!sourceFiles.contains(toBeAdded)) {
                    sourceFiles.add(toBeAdded);
                }
            }
        }


        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET: break; // keep default
            case INFO:
                logger.setLevel(Level.INFO); break;
            case DEBUG:
                logger.setLevel(Level.DEBUG); break;
            case TRACE:
                logger.setLevel(Level.TRACE); break;
            default:
                logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    /**
     * Displays a user manual for the command decac
     */
    protected void displayUsage() {
        System.out.println("Usage : decac [[-p | -v] [-n] [-r X] [-d]* [-P] <deca file>...] | [-b]");
        System.out.println("  -b (banner) : prints a banner indicating the team's name");
        System.out.println("  -p (parse) : stops decac after building the tree, " +
                "and prints the latter's decompilation (i.e. if there's only a single source file to compile, " +
                "the output must be a syntaxically correct deca file)");
        System.out.println("  -v (verification) : stops decac after the verification step " +
                "(does not produce an output if there is no error)");
        System.out.println("  -n (no check) : deletes overflow tests at execution" +
                "\n\t- arithmetical overflow \n\t- memory overflow \n\t- null dereferencement");
        System.out.println("  -r X (registers) : limits available registers to " +
                "R0 ... R{X-1}, with 4 <= X <= 16");
        System.out.println("  -d (debug) : enables debug traces." +
                " Repeat option to have more verbose traces.");
        System.out.println("  -P (parallel) : if there are several source files, " +
                "compiles them in parallel to gain compilation time");
    }
}
