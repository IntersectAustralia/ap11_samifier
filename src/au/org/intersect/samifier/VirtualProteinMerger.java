package au.org.intersect.samifier;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import au.org.intersect.samifier.runner.VirtualProteinMergerRunner;

public class VirtualProteinMerger implements Version {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("-v")) {
            System.out.println("Version = " + VERSION);
            System.exit(0);
        }
        OptionBuilder.hasArgs();
        OptionBuilder
                .withDescription("Mascot search results file in txt format");
        OptionBuilder.withArgName("searchResultsFile");
        OptionBuilder.isRequired();
        Option resultsFile = OptionBuilder.create("r");

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("Translation Table File");
        OptionBuilder
                .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.");
        Option translationTableOpt = OptionBuilder.create("t");

        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Directory containing the chromosome files in FASTA format for the given genome");
        OptionBuilder.withArgName("chromosomeDir");
        OptionBuilder.isRequired();
        Option chrDirOpt = OptionBuilder.create("c");

        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Genome file in gff format");
        OptionBuilder.withArgName("genomeFile");
        OptionBuilder.isRequired();
        Option genomeFileOpt = OptionBuilder.create("g");

        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Filename to write the gff file to");
        OptionBuilder.withArgName("outputFile");
        OptionBuilder.isRequired();
        Option outputFile = OptionBuilder.create("o");
        
        OptionBuilder.withType(Number.class);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withArgName("Confidence Score thresold");
        OptionBuilder.withDescription("Minimum confidence score for peptides to be included");
        Option score = OptionBuilder.create("s");

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(translationTableOpt);
        options.addOption(chrDirOpt);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);
        options.addOption(score);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            String[] searchResultsPaths = line.getOptionValues("r");
            File translationTableFile = new File(line.getOptionValue("t"));
            File genomeFile = new File(line.getOptionValue("g"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            Writer outputWriter = new FileWriter(outfile);
            String confidenceScoreOption = line.getOptionValue("s");
            BigDecimal confidenceScore = null;
            if (confidenceScoreOption != null) {
                confidenceScore = new BigDecimal(confidenceScoreOption);
            }
            
            VirtualProteinMergerRunner virtualProteinMergerRunner = new VirtualProteinMergerRunner(
                    searchResultsPaths, translationTableFile, genomeFile,
                    chromosomeDir, outputWriter, confidenceScore);
            virtualProteinMergerRunner.run();
        } catch (ParseException pe) {
            System.err.println("Version = " + VERSION);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("virtual_protein_merger", options, true);
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Version = " + VERSION);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("virtual_protein_merger", options, true);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
