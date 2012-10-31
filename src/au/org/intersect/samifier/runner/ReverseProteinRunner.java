package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.Samifier;
import au.org.intersect.samifier.domain.*;
import au.org.intersect.samifier.parser.*;

import java.io.File;
import java.util.List;

public class ReverseProteinRunner
{
    private String[] searchResultsPaths;
    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private File outputFile;
    private Genome genome;

    private ProteinToOLNMap proteinToOLNMap;

    public ReverseProteinRunner(String[] searchResultsPaths, File translationTableFile, File genomeFile, File chromosomeDir, File outputFile)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.chromosomeDir = chromosomeDir;
        this.translationTableFile = translationTableFile;
        this.outputFile = outputFile;
    }

    public void run() throws Exception
    {
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        genome = genomeParser.parseGenomeFile(genomeFile);

        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        proteinToOLNMap = new EqualProteinOLNMap();

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);
        CodonTranslationTable translationTable = CodonTranslationTable.parseTableFile(translationTableFile);

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults)
        {
            GeneInfo geneInfo = genome.getGene(peptideSearchResult.getProteinName());

            int virtualGeneStart = geneInfo.getStart()-1;
            int peptideStart = (peptideSearchResult.getPeptideStart() - 1) * 3;
            int peptideStop = (peptideSearchResult.getPeptideStop() - 1) * 3;

            File geneFile = new File(chromosomeDir, geneInfo.getChromosome() + ".faa");
            GenomeNucleotides genomeNucleotides = new GenomeNucleotides(geneFile);

            int proteinStart = virtualGeneStart + peptideStart;
            while ( !translationTable.isStartCodon(genomeNucleotides.codonAt(proteinStart)))
            {
                proteinStart += incrementStartPosition(geneInfo.getDirectionFlag());
            }

            int proteinEnd = virtualGeneStart + peptideStop;
            while ( !translationTable.isStopCodon(genomeNucleotides.codonAt(proteinEnd)))
            {
                proteinEnd += incrementStopPosition(geneInfo.getDirectionFlag());
            }

            ProteinLocation proteinLocation = new ProteinLocation("", proteinStart, proteinEnd, geneInfo.getDirection(), "", peptideSearchResult.getConfidenceScore());
            GffOutputter gffOutputter = new GffOutputter(proteinLocation, "");
        }
    }

    private int incrementStartPosition(int directionFlag)
    {
        return directionFlag == Samifier.SAM_REVERSE_FLAG ? 3 : -3;
    }

    private int incrementStopPosition(int directionFlag)
    {
        return directionFlag == Samifier.SAM_REVERSE_FLAG ? -3 : 3;
    }

}
