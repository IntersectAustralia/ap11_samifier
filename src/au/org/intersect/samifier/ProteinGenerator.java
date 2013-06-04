package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.ProteinGeneratorRunner;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ProteinGenerator implements Version {

    private ProteinGenerator() {
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("-v")) {
            System.out.println("Version = " + VERSION);
            System.exit(0);
        }
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("Translation Table File");
        OptionBuilder
                .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.");
        Option translationTableOpt = OptionBuilder.create("t");
        OptionBuilder.withArgName("Split Interval");
        OptionBuilder.hasArg();
        OptionBuilder.withType(Number.class);
        OptionBuilder.withDescription("Size of the intervals (number of codons) into which the genome will be split. Can't be used with the -g option.");
        Option splitIntervalOpt = OptionBuilder.create("i");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("Glimmer File");
        OptionBuilder.withDescription("Glimmer txt file. Can't be used with the -i option.");
        Option glimmerFileOpt = OptionBuilder.create("g");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Genome file in FASTA format");
        OptionBuilder.withArgName("Genome File");
        OptionBuilder.isRequired();
        Option genomeFileOpt = OptionBuilder.create("f");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Database name");
        OptionBuilder.withArgName("Database Name");
        OptionBuilder.isRequired();
        Option databaseNameOpt = OptionBuilder.create("d");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Filename to write the FASTA format file to");
        OptionBuilder.withArgName("Output File");
        OptionBuilder.isRequired();
        Option outputFileOpt = OptionBuilder.create("o");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Filename to write the GFF file to");
        OptionBuilder.withArgName("GFF File");
        OptionBuilder.isRequired(false);
        Option gffFileOpt = OptionBuilder.create("p");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Filename to write the accession file to");
        OptionBuilder.withArgName("Accession File");
        OptionBuilder.isRequired(false);
        Option accessionFileOpt = OptionBuilder.create("q");
        Options options = new Options();
        options.addOption(translationTableOpt);
        options.addOption(splitIntervalOpt);
        options.addOption(glimmerFileOpt);
        options.addOption(genomeFileOpt);
        options.addOption(databaseNameOpt);
        options.addOption(outputFileOpt);
        options.addOption(gffFileOpt);
        options.addOption(accessionFileOpt);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            File translationTableFile = new File(line.getOptionValue("t"));
            File genomeFile = new File(line.getOptionValue("f"));
            String glimmerFilePath = line.getOptionValue("g");
            String interval = line.getOptionValue("i");
            String databaseName = line.getOptionValue("d");
            File outfile = new File(line.getOptionValue("o"));
            Writer outputWriter = new FileWriter(outfile);

            String gffFilename = line.getOptionValue("p");
            Writer gffWriter = null;

            if (gffFilename != null) {
                File gffFile = new File(gffFilename);
                gffWriter = new FileWriter(gffFile);
            }

            String accessionFilename = line.getOptionValue("q");
            Writer accessionWriter = null;

            if (accessionFilename != null) {
                File accessionFile = new File(accessionFilename);
                accessionWriter = new FileWriter(accessionFile);
            }

            if ((glimmerFilePath == null && interval == null)
                    || (glimmerFilePath != null && interval != null)) {
                outputWriter.close();
                throw new ParseException("Only one of -i or -g permitted");
            }
            if (interval != null) {
                int intInterval = Integer.parseInt(interval);
                if (intInterval < 2) {
                    outputWriter.close();
                    throw new ParseException("Interval must be greater than  1");
                }
            }
            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(
                    glimmerFilePath, genomeFile, interval, databaseName,
                    outputWriter, translationTableFile, gffWriter,
                    accessionWriter);
            runner.run();
        } catch (ParseException pe) {
            System.err.println("Version = " + VERSION);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("protein_generator", options, true);
        } catch (Exception e) {
            System.err.println("Version = " + VERSION);
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
