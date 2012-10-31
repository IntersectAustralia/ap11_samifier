package au.org.intersect.samifier;

import au.org.intersect.samifier.runner.ReverseProteinRunner;
import org.apache.commons.cli.*;

import java.io.File;

public class ReverseProtein
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


        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(translationTableOpt);

        CommandLineParser parser = new GnuParser();
        try
        {
            CommandLine line = parser.parse( options, args );
            String[] searchResultsPaths = line.getOptionValues("r");
            File translationTableFile = new File(line.getOptionValue("t"));

            ReverseProteinRunner reverseProteinRunner = new ReverseProteinRunner(searchResultsPaths, translationTableFile, null, null, null);
            reverseProteinRunner.run();
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("reverse_protein", options, true);
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
