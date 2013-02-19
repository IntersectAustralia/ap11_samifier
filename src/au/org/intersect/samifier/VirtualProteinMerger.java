package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.VirtualProteinMergerRunner;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class VirtualProteinMerger {
    public static void main(String[] args) {
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

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(translationTableOpt);
        options.addOption(chrDirOpt);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            String[] searchResultsPaths = line.getOptionValues("r");
            File translationTableFile = new File(line.getOptionValue("t"));
            File genomeFile = new File(line.getOptionValue("g"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            Writer outputWriter = new FileWriter(outfile);

            VirtualProteinMergerRunner virtualProteinMergerRunner = new VirtualProteinMergerRunner(
                    searchResultsPaths, translationTableFile, genomeFile,
                    chromosomeDir, outputWriter);
            virtualProteinMergerRunner.run();
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("virtual_protein_merger", options, true);
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
