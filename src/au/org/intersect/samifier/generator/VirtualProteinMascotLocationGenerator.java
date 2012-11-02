package au.org.intersect.samifier.generator;

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
        System.out.println("Generating locations");
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        genome = genomeParser.parseGenomeFile(genomeFile);

        proteinToOLNMap = new EqualProteinOLNMap();

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);
        translationTable = CodonTranslationTable.parseTableFile(translationTableFile);

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults)
        {
            GeneInfo geneInfo = genome.getGene(peptideSearchResult.getProteinName());

            if (geneInfo == null)
            {
                System.err.println(peptideSearchResult.getProteinName() + " not found in the genome");
                continue;
            }

            int virtualGeneStart = geneInfo.getStart()-1;
            int virtualGeneStop = geneInfo.getStop()-1;

            int peptideAbsoluteStart;
            int peptideAbsoluteStop;

            int startOffset = (peptideSearchResult.getPeptideStart() - 1) * 3;
            int stopOffset = (peptideSearchResult.getPeptideStop() - 1) * 3;

            if (geneInfo.isForward())
            {
                peptideAbsoluteStart = virtualGeneStop - startOffset;
                peptideAbsoluteStop = virtualGeneStop - stopOffset;
            }
            else
            {
                peptideAbsoluteStart = virtualGeneStart + startOffset;
                peptideAbsoluteStop = virtualGeneStart + stopOffset;
            }

            File geneFile = new File(chromosomeDir, geneInfo.getChromosome() + ".faa");
            GenomeNucleotides genomeNucleotides = getGenomeNucleotides(geneFile);

            int startPosition = searchStart(peptideSearchResult, peptideAbsoluteStart, genomeNucleotides, geneInfo);
            int stopPosition = searchStop(peptideSearchResult, peptideAbsoluteStop, genomeNucleotides, geneInfo);

            if (startPosition == NOT_FOUND || stopPosition == NOT_FOUND)
            {
                continue;
            }

            proteinLocations.add(new ProteinLocation("?", startPosition, stopPosition-startPosition, geneInfo.getDirection(), "?", peptideSearchResult.getConfidenceScore()));
        }
        return proteinLocations;
    }

    private int searchStart(PeptideSearchResult peptideSearchResult, int proteinStart, GenomeNucleotides genomeNucleotides, GeneInfo geneInfo)
    {
        boolean reachedStart = false;
        int startIterator = proteinStart;
        String direction = geneInfo.getDirection();

        boolean isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator, direction));

        while ( !reachedStart && !isStartCodon)
        {
            startIterator += incrementStartPosition(geneInfo.getDirection());
            isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator, direction));
            reachedStart = reachedStart(startIterator, geneInfo.getDirection(), genomeNucleotides.getSize());
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
        String direction = geneInfo.getDirection();

        boolean isStopCodon = translationTable.isStopCodon(genomeNucleotides.codonAt(endIterator, direction));

        while ( !reachedStop && !isStopCodon)
        {
            endIterator += incrementStopPosition(geneInfo.getDirection());
            isStopCodon = translationTable.isStopCodon(genomeNucleotides.codonAt(endIterator, direction));
            reachedStop = reachedEnd(endIterator, geneInfo.getDirection(), genomeNucleotides.getSize());
        }

        if (reachedStop && !isStopCodon)
        {
            LOG.error("Reached end of sequence without finding stop codon for peptide " + peptideSearchResult.getPeptideSequence());
            return NOT_FOUND;
        }

        return endIterator;
    }

    private boolean reachedEnd(int position, String direction, int size)
    {
        if (GenomeConstant.REVERSE_FLAG.equals(direction))
        {
            return position <= 0;
        }
        else
        {
            return position + 3 >= size;
        }
    }

    private boolean reachedStart(int position, String direction, int size)
    {
        if (GenomeConstant.REVERSE_FLAG.equals(direction))
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

    private int incrementStartPosition(String direction)
    {
        return GenomeConstant.REVERSE_FLAG.equals(direction) ? GenomeConstant.BASES_PER_CODON : -GenomeConstant.BASES_PER_CODON;
    }

    private int incrementStopPosition(String direction)
    {
        return GenomeConstant.REVERSE_FLAG.equals(direction) ? -GenomeConstant.BASES_PER_CODON : GenomeConstant.BASES_PER_CODON;
    }

}
