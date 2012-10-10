package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.SamifierRunner;
import org.apache.commons.cli.*;
import java.io.*;
import org.apache.log4j.Logger;

public class Samifier
{

    private static Logger LOG = Logger.getLogger(Samifier.class);
    public static final int SAM_REVERSE_FLAG = 0x10;

    public static void main(String[] args)
    {
        Option resultsFile = OptionBuilder.withArgName("searchResultsFile")
                                          .hasArgs()
                                          .withDescription("Mascot search results file in txt format")
                                          .isRequired()
                                          .create("r");
        Option mappingFile = OptionBuilder.withArgName("mappingFile")
                                          .hasArg()
                                          .withDescription("File mapping protein identifier to ordered locus name")
                                          .isRequired()
                                          .create("m");
        Option genomeFileOpt = OptionBuilder.withArgName("genomeFile")
                                          .hasArg()
                                          .withDescription("Genome file in gff format")
                                          .isRequired()
                                          .create("g");
        Option chrDirOpt  = OptionBuilder.withArgName("chromosomeDir")
                                          .hasArg()
                                          .withDescription("Directory containing the chromosome files in FASTA format for the given genome")
                                          .isRequired()
                                          .create("c");
        Option logFile = OptionBuilder.withArgName("logFile")
                                          .hasArg()
                                          .isRequired(false)
                                          .withDescription("Filename to write the log into")
                                          .create("l");
        Option outputFile = OptionBuilder.withArgName("outputFile")
                                          .hasArg()
                                          .withDescription("Filename to write the SAM format file to")
                                          .isRequired()
                                          .create("o");
        Option bedFile = OptionBuilder.withArgName("bedFile")
                                          .hasArg()
                                          .isRequired(false)
                                          .withDescription("Filename to write IGV regions of interest (BED) file to")
                                          .create("b");

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(chrDirOpt);
        options.addOption(logFile);
        options.addOption(outputFile);
        options.addOption(bedFile);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            String[] searchResultsPaths = line.getOptionValues("r");
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            String logFileName = line.getOptionValue("l");
            String bedfilePath = line.getOptionValue("b");

            SamifierRunner samifier = new SamifierRunner(searchResultsPaths, genomeFile, mapFile, chromosomeDir, outfile, logFileName, bedfilePath);
            samifier.run();

        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("samifier", options, true);
            System.exit(1);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

}

