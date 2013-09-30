package com.wickedspiral.jacss;

import com.wickedspiral.jacss.lexer.Lexer;
import com.wickedspiral.jacss.parser.Parser;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author wasche
 * @since 2011.08.05
 */
public class JACSS implements Runnable
{
    public static class CLI
    {
        private static final String REGEX_FROM = "-gen.css$";
        private static final String REGEX_TO = "-c.css";
        private static final int NUM_THREADS = 1;

        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        @Argument(required=true, metaVar="FILE", usage="List of files to compress")
        private List<File> files;

        @Option(name="-r", aliases = {"--regex-from"}, required=false, metaVar="REGEXFROM",
                usage="Regex to replace with REGEXTO in new file names (default: " + REGEX_FROM + ")")
        private String regexFrom = REGEX_FROM;

        @Option(name="-t", aliases={"--regex-to"}, required=false, metaVar="REGEXTO",
                usage="Regex to replace REGEXFROM with, uses Java's Matcher.replace (default: " + REGEX_TO + ")")
        private String regexTo = REGEX_TO;

        @Option(name="-j", aliases={"--threads"}, required=false, metaVar="THREADS",
                usage="Number of threads to use (default: " + NUM_THREADS + ")")
        private int numThreads = NUM_THREADS;

        @Option(name="-v", aliases={"--verbose"}, required=false, metaVar="VERBOSE",
                usage="Print debugging information")
        private boolean verbose = false;

        @Option(name="-d", aliases={"--debug"}, required=false, metaVar="DEBUG",
                usage="Print additional debugging information")
        private boolean debug = false;

        @Option(name="-f", aliases={"--force"}, required=false, metaVar="FORCE",
                usage="Force re-compression")
        private boolean force = false;

        @Option(name = "-O", aliases = {"--stdout"}, required = false, usage = "Print to stdout instead of to file")
        private boolean stdout = false;

        @Option(name="--keep-trailing-semicolons", required = false,
                usage = "Do not strip commas on last style of a rule")
        private boolean keepTailingSemicolons = false;

        @Option(name = "--no-collapse-zeroes", required = false,
                usage = "Do not drop leading zero in floats less than 1")
        private boolean noCollapseZeroes = false;

        @Option(name = "--no-collapse-none", required = false,
                usage = "Do not collapse none to 0")
        private boolean noCollapseNone = false;

        public Pattern getFromPattern()
        {
            return Pattern.compile(regexFrom == null ? REGEX_FROM : regexFrom);
        }
    }

    private static final int EXIT_STATUS_INVALID_ARG = 1;
    private static final int EXIT_STATUS_INVALID_FILE = 2;
    private static final int EXIT_STATUS_TIMEOUT = 3;
    private static final int EXIT_STATUS_COMPRESSION_FAILED = 4;
    
    private static final AtomicInteger numFailures = new AtomicInteger();

    private String sourceName;
    private String targetName;
    private InputStream source;
    private OutputStream target;
    private CLI cli;
    private boolean shouldCompress = true;

    public JACSS(File file, Pattern from, CLI cli) throws FileNotFoundException
    {
        this.cli = cli;

        if ( !file.isFile() )
        {
            throw new FileNotFoundException(source.toString());
        }

        sourceName = file.getName();
        targetName = from.matcher(source.toString()).replaceAll(cli.regexTo);
        File toFile = new File( targetName );

        if ( toFile.exists() && toFile.lastModified() >= file.lastModified() )
        {
            shouldCompress = cli.force;
        }

        source = new FileInputStream( file );
        target = cli.stdout ? System.out : new FileOutputStream( targetName );
    }

    public JACSS( InputStream source, OutputStream target, CLI cli ) throws FileNotFoundException
    {
        this.sourceName = "<in>";
        this.targetName = "<out>";
        this.source = source;
        this.target = target;
        this.cli = cli;
    }

    public void run()
    {
        if ( shouldCompress )
        {
            if (cli.verbose) System.err.println( "Compressing " + sourceName + " to " + targetName );

            try(
                    BufferedInputStream in = new BufferedInputStream( source );
                    PrintStream out = new PrintStream( new BufferedOutputStream( target ) )
            )
            {
                Parser parser = new Parser(
                        out,
                        cli.debug, cli.keepTailingSemicolons, cli.noCollapseZeroes, cli.noCollapseNone
                );
                Lexer lexer = new Lexer();
                lexer.addTokenListener(parser);
                
                lexer.parse( in );
            }
            catch (Exception e)
            {
                numFailures.incrementAndGet();
                System.err.println("Compression failed for " + sourceName);
                e.printStackTrace( System.err );
            }
        }
        else
        {
            if (cli.verbose) System.err.println("Skipping " + targetName);
        }
    }

    public static void main(String[] args)
    {
        CLI cli = new CLI();
        CmdLineParser parser = new CmdLineParser(cli);
        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(EXIT_STATUS_INVALID_ARG);
        }

        Pattern from = cli.getFromPattern();

        if (cli.debug) System.err.println("Debug mode enabled.");

        ExecutorService pool = Executors.newFixedThreadPool(cli.numThreads);
        for (File file : cli.files)
        {
            try
            {
                pool.submit(new JACSS(file, from, cli));
            }
            catch (FileNotFoundException e)
            {
                System.err.println("Could not find file: " + e.getMessage());
                pool.shutdownNow();
                System.exit(EXIT_STATUS_INVALID_FILE);
            }
        }

        pool.shutdown();

        try
        {
            pool.awaitTermination(2, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            System.err.println("ERROR: Timed out waiting for threads to finish.");
            System.exit(EXIT_STATUS_TIMEOUT);
        }
        
        System.exit(numFailures.get() == 0 ? 0 : EXIT_STATUS_COMPRESSION_FAILED);
    }
}
