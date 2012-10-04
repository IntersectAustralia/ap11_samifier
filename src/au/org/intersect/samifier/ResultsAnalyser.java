package au.org.intersect.samifier;

import au.org.intersect.samifier.mascot.PeptideSearchResultsParser;
import au.org.intersect.samifier.mascot.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.mascot.PeptideSequenceGenerator;
import au.org.intersect.samifier.mascot.PeptideSequenceGeneratorImpl;
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
    private Genome genome;
    private Map<String, String> proteinToOLNMap;
    private List<PeptideSearchResult> peptideSearchResults;
    private File outputFile;
    private File chromosomeDir;
    private PeptideSequenceGenerator sequenceGenerator;

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

        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);
        options.addOption(chrDirOpt);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            File searchResultsFile = new File(line.getOptionValue("r"));
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File outfile = new File(line.getOptionValue("o"));
            File chromosomeDir = new File(line.getOptionValue("c"));

            ResultsAnalyser analyser = new ResultsAnalyser(searchResultsFile, genomeFile, mapFile, outfile, chromosomeDir);
            analyser.createResultAnalysis();
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

    protected ResultsAnalyser(File searchResultsFile, File genomeFile, File mapFile, File outputFile, File chromosomeDir) throws Exception
    {
        this.genome = Genome.parse(genomeFile);
        this.proteinToOLNMap = Samifier.parseProteinToOLNMappingFile(mapFile);
        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        this.peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsFile);
        this.outputFile = outputFile;
        this.chromosomeDir = chromosomeDir;
        sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinToOLNMap, chromosomeDir);

    }

    protected void createResultAnalysis() throws Exception
    {
        FileWriter output = new FileWriter(outputFile);

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults)
        {
            PeptideSequence peptideSequence = sequenceGenerator.getPeptideSequence(peptideSearchResult);
            ResultsAnalyserOutputter outputter = new ResultsAnalyserOutputter(peptideSearchResult, proteinToOLNMap, genome, peptideSequence);
            output.write(outputter.toString());
            output.write(System.getProperty("line.separator"));
        }
        output.close();
    }
}
