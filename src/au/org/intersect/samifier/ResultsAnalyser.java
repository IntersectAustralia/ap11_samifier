package au.org.intersect.samifier;

import au.org.intersect.samifier.mascot.PeptideSearchResultsParser;
import au.org.intersect.samifier.mascot.PeptideSearchResultsParserImpl;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 26/09/12
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultsAnalyser
{
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
        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            File searchResultsFile = new File(line.getOptionValue("r"));
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File outfile = new File(line.getOptionValue("o"));

            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(map);

            List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsFile);

            FileWriter resultAnalysis = new FileWriter(outfile);

            ResultsAnalyser.createResultAnalysis(genome, map, peptideSearchResults, resultAnalysis);
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("result_analyser", options, true);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }

    }

    protected static void createResultAnalysis(Genome genome, Map<String,String> map, List<PeptideSearchResult> peptideSearchResults, FileWriter output) throws IOException {
        final String SEPARATOR = "\t";

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults)
        {
            output.write(peptideSearchResult.getProteinName() + SEPARATOR);
            output.write( map.get(peptideSearchResult.getProteinName()) + SEPARATOR);
            // This one is the "Gene ID" and is to be confirmed
            output.write( map.get(peptideSearchResult.getProteinName()) + SEPARATOR);
            output.write(peptideSearchResult.getConfidenceScore().toString() + SEPARATOR);
            output.write(Integer.toString(peptideSearchResult.getPeptideStart()) + SEPARATOR);
            output.write(Integer.toString(peptideSearchResult.getPeptideStop()) + SEPARATOR);
            output.write(Integer.toString(peptideSearchResult.getSequenceLength()));

            output.write(System.getProperty("line.separator"));
        }
        output.close();
    }
}
