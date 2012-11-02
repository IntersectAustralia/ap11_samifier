package au.org.intersect.samifier;

import au.org.intersect.samifier.reporter.DatabaseHelper;
import au.org.intersect.samifier.runner.ResultAnalyserRunner;
import org.apache.commons.cli.*;

import java.io.File;
import java.sql.SQLException;

public class ResultsAnalyser
{
    private File searchResultsFile;
    private File genomeFile;
    private File proteinToOLNMapFile;
    private File outputFile;
    private File chromosomeDir;
    private String sqlQuery;
    
    public static void main(String ... args)
    {
        Option resultsFile = OptionBuilder.withArgName("searchResultsFile")
                .hasArg()
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
        Option outputFile = OptionBuilder.withArgName("outputFile")
                .hasArg()
                .withDescription("Filename to write the SAM format file to")
                .isRequired()
                .create("o");
        Option chrDirOpt  = OptionBuilder.withArgName("chromosomeDir")
                .hasArg()
                .withDescription("Directory containing the chromosome files in FASTA format for the given genome")
                .isRequired()
                .create("c");
        Option sqlOpt  = OptionBuilder.withArgName("sqlQuery")
                .hasArg()
                .withDescription("Filters the result through the use of a SQL statement to the output file")
                .create("sql");
        
        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);
        options.addOption(chrDirOpt);
        options.addOption(sqlOpt);
        
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            File searchResultsFile = new File(line.getOptionValue("r"));
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File outfile = new File(line.getOptionValue("o"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            String sqlQuery = line.getOptionValue("sql");

            ResultAnalyserRunner analyser = new ResultAnalyserRunner(searchResultsFile, genomeFile, mapFile, outfile, chromosomeDir, sqlQuery);
            
            if (sqlQuery.isEmpty())
            {
            	analyser.run();	
            }
            else
            {
            	analyser.initMemoryDb();
            	analyser.runWithQuery();	
            } 	     	          
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("result_analyser", options, true);
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
