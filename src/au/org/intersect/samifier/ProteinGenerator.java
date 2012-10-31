package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.ProteinGeneratorRunner;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class ProteinGenerator {

    private ProteinGenerator(){}



    public static void main(String[] args)
    {
        Option translationTableOpt =
            OptionBuilder.withArgName("Translation Table File")
                         .hasArg()
                         .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.")
                         .create("t");
        Option splitIntervalOpt =
            OptionBuilder.withArgName("Split Interval")
                         .hasArg()
                         .withType(Number.class)
                         .withDescription("Size of the intervals (number of codons) into which the genome will be split. Can't be used with the -g option.")
                         .create("i");
        Option glimmerFileOpt =
            OptionBuilder.withArgName("Glimmer File")
                         .hasArg()
                         .withDescription("Glimmer txt file. Can't be used with the -i option.")
                         .create("g");
        Option genomeFileOpt =
            OptionBuilder.withArgName("Genome File")
                         .hasArg()
                         .withDescription("Genome file in FASTA format")
                         .isRequired()
                         .create("f");
        Option databaseNameOpt =
            OptionBuilder.withArgName("Database Name")
                         .hasArg()
                         .withDescription("Database name")
                         .isRequired()
                         .create("d");
        Option outputFileOpt =
            OptionBuilder.withArgName("Output File")
                         .hasArg()
                         .withDescription("Filename to write the FASTA format file to")
                         .isRequired()
                         .create("o");
        Option gffFileOpt =
                OptionBuilder.withArgName("GFF File")
                        .hasArg()
                        .withDescription("Filename to write the GFF file to")
                        .isRequired(false)
                        .create("p");
        Option accessionFileOpt =
                OptionBuilder.withArgName("Accession File")
                        .hasArg()
                        .withDescription("Filename to write the accession file to")
                        .isRequired(false)
                        .create("q");
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
            CommandLine line = parser.parse( options, args );
            File translationTableFile = new File(line.getOptionValue("t"));
            File genomeFile = new File(line.getOptionValue("f"));
            String glimmerFilePath = line.getOptionValue("g");
            String interval = line.getOptionValue("i");
            String databaseName = line.getOptionValue("d");
            File outfile = new File(line.getOptionValue("o"));
            Writer outputWriter = new FileWriter(outfile);

            String gffFilename = line.getOptionValue("p");
            Writer gffWriter = null;

            if (gffFilename != null)
            {
                File gffFile = new File(gffFilename);
                gffWriter = new FileWriter(gffFile);
            }

            String accessionFilename = line.getOptionValue("q");
            Writer accessionWriter = null;

            if (accessionFilename != null)
            {
                File accessionFile = new File(accessionFilename);
                accessionWriter = new FileWriter(accessionFile);
            }

            if ((glimmerFilePath == null && interval == null) ||
                (glimmerFilePath != null && interval != null))
            {
                throw new ParseException("Only one of -i or -g permitted");
            }
            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(glimmerFilePath, genomeFile, interval,
                    databaseName, outputWriter, translationTableFile, gffWriter, accessionWriter);
            runner.run();
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("protein_generator", options, true);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}

