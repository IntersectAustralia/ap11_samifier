package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.Samifier;
import au.org.intersect.samifier.domain.*;
import au.org.intersect.samifier.parser.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VirtualProteinMascotLocationGenerator implements LocationGenerator
{
    private static Logger LOG = Logger.getLogger(VirtualProteinMascotLocationGenerator.class);

    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private String[] searchResultsPaths;

    private Genome genome;
    private ProteinToOLNMap proteinToOLNMap;

    public VirtualProteinMascotLocationGenerator(String[] searchResultsPaths, File translationTableFile, File genomeFile, File chromosomeDir)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.chromosomeDir = chromosomeDir;
        this.translationTableFile = translationTableFile;
    }

    @Override
    public List<ProteinLocation> generateLocations() throws LocationGeneratorException
    {
        try
        {
            return doGenerateLocations();
        }
        catch (TranslationTableParsingException e)
        {
            throw new LocationGeneratorException("Error parsing translation table file", e);
        }
        catch (MascotParsingException e)
        {
            throw new LocationGeneratorException("Error parsing mascot file", e);
        }
        catch (GenomeFileParsingException e)
        {
            throw new LocationGeneratorException("Error parsing genome file", e);
        }
        catch (IOException e)
        {
            throw new LocationGeneratorException("Error parsing genome file", e);
        }

    }

    public List<ProteinLocation> doGenerateLocations()
            throws GenomeFileParsingException, MascotParsingException, IOException, TranslationTableParsingException
    {
        List<ProteinLocation> proteinLocations = new ArrayList<ProteinLocation>();

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

            proteinLocations.add(new ProteinLocation("?", proteinStart, proteinEnd, geneInfo.getDirection(), "?", peptideSearchResult.getConfidenceScore()));
        }
        return proteinLocations;
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
