package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            System.err.println();
            options.displayUsage();
            System.exit(1);
        }

        // Differents things to do depending on the provided CompilerOptions

        if (options.getPrintBanner()) {
            System.out.println();
            System.out.println(" #####                                         #####  #            ###     #\n"+
                             "#     # #####   ####  #    # #####  ######    #     # #           #   #   ##\n"+
                             "#       #    # #    # #    # #    # #         #       #          #     # # # \n"+
                             "#  #### #    # #    # #    # #    # #####     #  #### #          #     #   #  \n"+
                             "#     # #####  #    # #    # #####  #         #     # #          #     #   # \n"+
                             "#     # #   #  #    # #    # #      #         #     # #           #   #    # \n"+
                             " #####  #    #  ####   ####  #      ######     #####  #######      ###   #####");
            System.out.println();
            System.out.println("Clément Malleret - Mathieu David - " +
                    "Tiphaine Helmer - Rémi Granotier - Isabella Costa Maia");
            System.exit(0);
        }

        if (options.getSourceFiles().isEmpty()) {
            System.out.println("Missing source file argument\n");
            options.displayUsage();
        }

        if (options.getParallel()) {
            Boolean compilationError = false;
            ExecutorService pool = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors());
            for (File source : options.getSourceFiles()) {
                Future<Boolean> result = pool.submit(new Callable<Boolean>() {
                    public Boolean call() {
                        DecacCompiler compiler = new DecacCompiler(options, source);
                        return compiler.compile();
                    }
                });
                try {
                    compilationError = result.get();
                } catch (InterruptedException e) {
                    System.err.println("Computation cancelled while waiting :\n" + e.getMessage());
                    System.exit(1);
                } catch (ExecutionException e) {
                    System.err.println("Computation threw an exception :\n" + e.getMessage());
                    System.exit(1);
                }
                if (compilationError) {
                    error = true;
                }
            }
            pool.shutdown();
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
