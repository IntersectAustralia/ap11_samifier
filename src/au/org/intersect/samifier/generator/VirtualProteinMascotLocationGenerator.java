package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.Samifier;
import au.org.intersect.samifier.domain.*;
import au.org.intersect.samifier.parser.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualProteinMascotLocationGenerator implements LocationGenerator
{
    private static Logger LOG = Logger.getLogger(VirtualProteinMascotLocationGenerator.class);
    private static int NOT_FOUND = -1;

    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private String[] searchResultsPaths;

    private Genome genome;
    private ProteinToOLNMap proteinToOLNMap;
    private Map<String, GenomeNucleotides> genomeNucleotidesMap = new HashMap<String, GenomeNucleotides>();
    private CodonTranslationTable translationTable;

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

        proteinToOLNMap = new EqualProteinOLNMap();

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);
        translationTable = CodonTranslationTable.parseTableFile(translationTableFile);

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults)
        {
            GeneInfo geneInfo = genome.getGene(peptideSearchResult.getProteinName());

            int virtualGeneStart = geneInfo.getStart()-1;
            int peptideStart = (peptideSearchResult.getPeptideStart() - 1) * 3;
            int peptideStop = (peptideSearchResult.getPeptideStop() - 1) * 3;

            File geneFile = new File(chromosomeDir, geneInfo.getChromosome() + ".faa");
            GenomeNucleotides genomeNucleotides = getGenomeNucleotides(geneFile);

            int startPosition = searchStart(peptideSearchResult, virtualGeneStart + peptideStart, genomeNucleotides, geneInfo);
            int stopPosition = searchStop(peptideSearchResult, virtualGeneStart + peptideStop, genomeNucleotides, geneInfo);
            if (startPosition == NOT_FOUND || stopPosition == NOT_FOUND)
            {
                continue;
            }

            proteinLocations.add(new ProteinLocation("?", startPosition, stopPosition, geneInfo.getDirection(), "?", peptideSearchResult.getConfidenceScore()));
        }
        return proteinLocations;
    }

    private int searchStart(PeptideSearchResult peptideSearchResult, int proteinStart, GenomeNucleotides genomeNucleotides, GeneInfo geneInfo)
    {
        boolean reachedStart = false;
        int startIterator = proteinStart;

        boolean isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator));

        while ( !reachedStart && !isStartCodon)
        {
            startIterator += incrementStartPosition(geneInfo.getDirectionFlag());
            isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator));
            reachedStart = reachedStart(startIterator, geneInfo.getDirectionFlag(), genomeNucleotides.getSize());
        }

        if (reachedStart && !isStartCodon)
        {
            LOG.error("Reached beginning of sequence without finding start codon for peptide " + peptideSearchResult.getPeptideSequence());
            return NOT_FOUND;
        }

        return startIterator;
    }

    private int searchStop(PeptideSearchResult peptideSearchResult, int proteinEnd, GenomeNucleotides genomeNucleotides, GeneInfo geneInfo)
    {
        boolean reachedStop = false;
        int endIterator = proteinEnd;

        boolean isStopCodon = translationTable.isStopCodon(genomeNucleotides.codonAt(endIterator));

        while ( !reachedStop && !isStopCodon)
        {
            endIterator += incrementStopPosition(geneInfo.getDirectionFlag());
            isStopCodon = translationTable.isStopCodon(genomeNucleotides.codonAt(endIterator));
            reachedStop = reachedEnd(endIterator, geneInfo.getDirectionFlag(), genomeNucleotides.getSize());
        }

        if (reachedStop && !isStopCodon)
        {
            LOG.error("Reached end of sequence without finding stop codon for peptide " + peptideSearchResult.getPeptideSequence());
            return NOT_FOUND;
        }

        return endIterator;
    }

    private boolean reachedEnd(int position, int directionFlag, int size)
    {
        if (directionFlag == Samifier.SAM_REVERSE_FLAG)
        {
            return position <= 0;
        }
        else
        {
            return position + 3 >= size;
        }
    }

    private boolean reachedStart(int position, int directionFlag, int size)
    {
        if (directionFlag == Samifier.SAM_REVERSE_FLAG)
        {
            return position + 3 >= size;
        }
        else
        {
            return position <= 0;
        }
    }

    private GenomeNucleotides getGenomeNucleotides(File geneFile) throws IOException
    {
        if (!genomeNucleotidesMap.containsKey(geneFile))
        {
            genomeNucleotidesMap.put(geneFile.getName(), new GenomeNucleotides(geneFile));
        }
        return genomeNucleotidesMap.get(geneFile.getName());
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
