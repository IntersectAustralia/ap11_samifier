package au.org.intersect.samifier;

import au.org.intersect.samifier.generator.PeptideSequenceGenerator;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;
import au.org.intersect.samifier.outputter.ResultsAnalyserOutputter;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
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
    private File searchResultsFile;
    private File genomeFile;
    private File proteinToOLNMapFile;
    private File outputFile;
    private File chromosomeDir;

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
            System.exit(1);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

    }

    protected ResultsAnalyser(File searchResultsFile, File genomeFile, File proteinToOLNMapFile, File outputFile, File chromosomeDir) throws Exception
    {
        this.searchResultsFile = searchResultsFile;
        this.genomeFile = genomeFile;
        this.proteinToOLNMapFile = proteinToOLNMapFile;
        this.outputFile = outputFile;
        this.chromosomeDir = chromosomeDir;
    }

    protected void createResultAnalysis() throws Exception
    {
        Genome genome = Genome.parse(genomeFile);

        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        Map<String, String> proteinToOLNMap = proteinToOLNParser.parseMappingFile(proteinToOLNMapFile);

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsFile);
        PeptideSequenceGenerator sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinToOLNMap, chromosomeDir);

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
