package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.Genome;
import au.org.intersect.samifier.PeptideSearchResult;
import au.org.intersect.samifier.PeptideSequence;
import au.org.intersect.samifier.generator.PeptideSequenceGenerator;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;
import au.org.intersect.samifier.outputter.ResultsAnalyserOutputter;

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
public class ResultAnalyserRunner
{
    private File searchResultsFile;
    private File genomeFile;
    private File proteinToOLNMapFile;
    private File outputFile;
    private File chromosomeDir;

    public ResultAnalyserRunner(File searchResultsFile, File genomeFile, File proteinToOLNMapFile, File outputFile, File chromosomeDir) throws Exception
    {
        this.searchResultsFile = searchResultsFile;
        this.genomeFile = genomeFile;
        this.proteinToOLNMapFile = proteinToOLNMapFile;
        this.outputFile = outputFile;
        this.chromosomeDir = chromosomeDir;
    }

    public void run() throws Exception
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
