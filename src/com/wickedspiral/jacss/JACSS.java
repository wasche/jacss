package com.wickedspiral.jacss;

import com.wickedspiral.jacss.lexer.Lexer;
import com.wickedspiral.jacss.lexer.UnrecognizedCharacterException;
import com.wickedspiral.jacss.parser.Parser;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author wasche
 * @since 2011.08.05
 */
public class JACSS implements Runnable
{
    private static class CLI
    {
        private static final String REGEX_FROM = "-gen.css$";
        private static final String REGEX_TO = "-c.css";
        private static final int NUM_THREADS = 1;

        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        @Argument(required=true, metaVar="FILE", usage="List of files to compress")
        private List<File> files;

        @Option(name="-r", aliases = {"--regex-from"}, required=false, metaVar="REGEXFROM",
                usage="Regex to replace with REGEXTO in new file names (default: " + REGEX_FROM + ")")
        private String regexFrom;

        @Option(name="-t", aliases={"--regex-to"}, required=false, metaVar="REGEXTO",
                usage="Regex to replace REGEXFROM with, uses Java's Matcher.replace (default: " + REGEX_TO + ")")
        private String regexTo;

        @Option(name="-j", aliases={"--threads"}, required=false, metaVar="THREADS",
                usage="Number of threads to use (default: " + NUM_THREADS + ")")
        private int numThreads;

        @Option(name="-v", aliases={"--verbose"}, required=false, metaVar="VERBOSE",
                usage="Print debugging information")
        private boolean verbose;

        public Pattern getFromPattern()
        {
            return Pattern.compile(regexFrom == null ? REGEX_FROM : regexFrom);
        }

        public int getNumThreads()
        {
            return numThreads > 0 ? numThreads : NUM_THREADS;
        }
    }

    private static final int EXIT_STATUS_INVALID_ARG = 1;
    private static final int EXIT_STATUS_INVALID_FILE = 2;
    private static final int EXIT_STATUS_TIMEOUT = 3;

    private File source;
    private File target;
    private FileInputStream in;
    private FileOutputStream out;

    public JACSS(File file, Pattern from, String to) throws FileNotFoundException
    {
        source = file;

        if (!source.isFile())
        {
            throw new FileNotFoundException(source.toString());
        }

        in = new FileInputStream(source);
        out = new FileOutputStream(target);

        String filename = from.matcher(source.toString()).replaceAll(to);
        target = new File(filename);
    }

    public void run()
    {
        if (!target.exists() || target.lastModified() < source.lastModified())
        {
            // debug Compressing + target
            Parser parser = new Parser();
            Lexer lexer = new Lexer();
            lexer.addTokenListener(parser);
            try
            {
                lexer.parse(in);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (UnrecognizedCharacterException e)
            {
                e.printStackTrace();
            }
            finally {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    //
                }
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    //
                }
            }
        }
        else
        {
            // debug Skipping + target
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

        ExecutorService pool = Executors.newFixedThreadPool(cli.getNumThreads());
        for (File file : cli.files)
        {
            try
            {
                pool.submit(new JACSS(file, from, cli.regexTo));
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
            System.exit(EXIT_STATUS_TIMEOUT);
        }
    }
}
