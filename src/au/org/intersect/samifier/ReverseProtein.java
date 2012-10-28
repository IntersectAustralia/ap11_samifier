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

        Option mappingFile = OptionBuilder.withArgName("mappingFile")
                .hasArg()
                .withDescription("File mapping protein identifier to ordered locus name")
                .isRequired()
                .create("m");

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);

        CommandLineParser parser = new GnuParser();
        try
        {
            CommandLine line = parser.parse( options, args );
            String[] searchResultsPaths = line.getOptionValues("r");
            File mapFile = new File(line.getOptionValue("m"));

            ReverseProteinRunner reverseProteinRunner = new ReverseProteinRunner(searchResultsPaths, mapFile);
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
