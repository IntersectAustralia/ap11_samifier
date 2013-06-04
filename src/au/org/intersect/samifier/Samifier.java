package au.org.intersect.samifier;

import java.io.File;
import java.math.BigDecimal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import au.org.intersect.samifier.domain.DebuggingFlag;
import au.org.intersect.samifier.runner.SamifierRunner;

public class Samifier implements Version {

    public static final int SAM_REVERSE_FLAG = 0x10;

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("-v")) {
            System.out.println("Version = " + VERSION);
            System.exit(0);
        }
        OptionBuilder.hasArgs();
        OptionBuilder.withDescription("Mascot search results file in txt format");
        OptionBuilder.withArgName("searchResultsFile");
        OptionBuilder.isRequired();
        Option resultsFile = OptionBuilder.create("r");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("File mapping protein identifier to ordered locus name");
        OptionBuilder.withArgName("mappingFile");
        OptionBuilder.isRequired();
        Option mappingFile = OptionBuilder.create("m");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Genome file in gff format");
        OptionBuilder.withArgName("genomeFile");
        OptionBuilder.isRequired();
        Option genomeFileOpt = OptionBuilder.create("g");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Directory containing the chromosome files in FASTA format for the given genome");
        OptionBuilder.withArgName("chromosomeDir");
        OptionBuilder.isRequired();
        Option chrDirOpt = OptionBuilder.create("c");
        OptionBuilder.isRequired(false);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("logFile");
        OptionBuilder.withDescription("Filename to write the log into");
        Option logFile = OptionBuilder.create("l");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Filename to write the SAM format file to");
        OptionBuilder.withArgName("outputFile");
        OptionBuilder.isRequired();
        Option outputFile = OptionBuilder.create("o");
        OptionBuilder.isRequired(false);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("bedFile");
        OptionBuilder.withDescription("Filename to write IGV regions of interest (BED) file to");
        Option bedFile = OptionBuilder.create("b");
        OptionBuilder.withType(Number.class);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withArgName("Confidence Score thresold");
        OptionBuilder.withDescription("Minimum confidence score for peptides to be included");
        Option score = OptionBuilder.create("s");

        Options options = new Options();

        if (DebuggingFlag.get_sbi_debug_flag() == 1) {
            OptionBuilder.hasArg();
            OptionBuilder
                    .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.");
            OptionBuilder.withArgName("Translation Table File");
            OptionBuilder.isRequired();
            Option translationTableOpt = OptionBuilder.create("t");
            options.addOption(translationTableOpt);
        }

        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(chrDirOpt);
        options.addOption(logFile);
        options.addOption(outputFile);
        options.addOption(bedFile);
        options.addOption(score);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            String[] searchResultsPaths = line.getOptionValues("r");
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            String logFileName = line.getOptionValue("l");
            String bedfilePath = line.getOptionValue("b");
            String confidenceScoreOption = line.getOptionValue("s");
            BigDecimal confidenScore = null;
            if (confidenceScoreOption != null) {
                confidenScore = new BigDecimal(confidenceScoreOption);
            }

            if (logFileName != null) {
                setFileLogger(logFileName);
            }

            if (DebuggingFlag.get_sbi_debug_flag() == 1) {
                File translationTableFile = new File(line.getOptionValue("t"));
                SamifierRunner samifier = new SamifierRunner(
                        searchResultsPaths, genomeFile, mapFile, chromosomeDir,
                        outfile, bedfilePath, confidenScore,
                        translationTableFile);
                samifier.run();
            } else {
                SamifierRunner samifier = new SamifierRunner(
                        searchResultsPaths, genomeFile, mapFile, chromosomeDir,
                        outfile, bedfilePath, confidenScore);
                samifier.run();
            }

        } catch (ParseException pe) {
            System.err.println(pe);
            System.err.println("Version = " + VERSION);
            // System.err.println(pe.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("samifier", options, true);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Version = " + VERSION);
            System.err.println(e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("samifier", options, true);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void setFileLogger(String logFileName) {
        Logger.getRootLogger().removeAppender("stdout");
        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(logFileName);
        fa.setLayout(new PatternLayout("%d %-5p %c - %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        Logger.getRootLogger().addAppender(fa);
    }

}
