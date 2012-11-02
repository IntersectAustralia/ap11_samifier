package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.VirtualProteinMergerRunner;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class VirtualProteinMerger
{
    public static void main(String[] args)
    {
        Option resultsFile = OptionBuilder.withArgName("searchResultsFile")
            .hasArgs()
            .withDescription("Mascot search results file in txt format")
            .isRequired()
            .create("r");

        Option translationTableOpt =
                OptionBuilder.withArgName("Translation Table File")
                        .hasArg()
                        .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.")
                        .create("t");

        Option chrDirOpt  = OptionBuilder.withArgName("chromosomeDir")
                .hasArg()
                .withDescription("Directory containing the chromosome files in FASTA format for the given genome")
                .isRequired()
                .create("c");

        Option genomeFileOpt = OptionBuilder.withArgName("genomeFile")
                .hasArg()
                .withDescription("Genome file in gff format")
                .isRequired()
                .create("g");

        Option outputFile = OptionBuilder.withArgName("outputFile")
                .hasArg()
                .withDescription("Filename to write the gff file to")
                .isRequired()
                .create("o");

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(translationTableOpt);
        options.addOption(chrDirOpt);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);

        CommandLineParser parser = new GnuParser();
        try
        {
            CommandLine line = parser.parse( options, args );
            String[] searchResultsPaths = line.getOptionValues("r");
            File translationTableFile = new File(line.getOptionValue("t"));
            File genomeFile = new File(line.getOptionValue("g"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            Writer outputWriter = new FileWriter(outfile);

            VirtualProteinMergerRunner virtualProteinMergerRunner =
                    new VirtualProteinMergerRunner(searchResultsPaths, translationTableFile, genomeFile, chromosomeDir, outputWriter);
            virtualProteinMergerRunner.run();
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("virtual_protein_merger", options, true);
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
