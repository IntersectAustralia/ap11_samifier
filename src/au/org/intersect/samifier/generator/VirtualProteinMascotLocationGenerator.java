package au.org.intersect.samifier.generator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.EqualProteinOLNMap;
import au.org.intersect.samifier.domain.GeneInfo;
import au.org.intersect.samifier.domain.Genome;
import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.GenomeNucleotides;
import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import au.org.intersect.samifier.domain.TranslationTableParsingException;
import au.org.intersect.samifier.filter.ConfidenceScoreFilter;
import au.org.intersect.samifier.filter.PeptideSearchResultFilter;
import au.org.intersect.samifier.parser.FastaParser;
import au.org.intersect.samifier.parser.FastaParserException;
import au.org.intersect.samifier.parser.FastaParserImpl;
import au.org.intersect.samifier.parser.GenomeFileParsingException;
import au.org.intersect.samifier.parser.GenomeParserImpl;
import au.org.intersect.samifier.parser.MascotParsingException;
import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;

public class VirtualProteinMascotLocationGenerator implements LocationGenerator {
    private static Logger LOG = Logger.getLogger(VirtualProteinMascotLocationGenerator.class);
    private static int NOT_FOUND = -1;

    private static final int MIN_PROTEIN_LENGTH = 5;
    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private String[] searchResultsPaths;
    private FastaParser fastaParser;
    private PeptideSearchResultFilter peptideFilter;

    private Genome genome;
    private ProteinToOLNMap proteinToOLNMap;
    private Map<String, GenomeNucleotides> genomeNucleotidesMap = new HashMap<String, GenomeNucleotides>();
    private CodonTranslationTable translationTable;

    public VirtualProteinMascotLocationGenerator(String[] searchResultsPaths,
            File translationTableFile, File genomeFile, File chromosomeDir, BigDecimal confidenceScore) {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.chromosomeDir = chromosomeDir;
        this.translationTableFile = translationTableFile;
        if (confidenceScore != null) {
            peptideFilter = new ConfidenceScoreFilter(confidenceScore);
        }
    }

    @Override
    public List<ProteinLocation> generateLocations()
            throws LocationGeneratorException {
        try {
            this.fastaParser = new FastaParserImpl(chromosomeDir);
            List<ProteinLocation> locations = doGenerateLocations();
            locations = removeDuplicates(locations);
            locations = mergeProteins(locations);
            Collections.sort(locations);
            Iterator<ProteinLocation> setIterator = locations.iterator();
            while (setIterator.hasNext()) {
                ProteinLocation currentElement = setIterator.next();
                if (currentElement.getLength() < MIN_PROTEIN_LENGTH) {
                    setIterator.remove();
                }
            }
            if (locations.size() > 1) {
              //remove all proteins that are contained inside bigger protein
                setIterator = locations.iterator();
                ProteinLocation base = setIterator.next();
                while (setIterator.hasNext()) {
                    ProteinLocation currentElement = setIterator.next();
                    if (currentElement.getStartIndex() >= base.getStartIndex() && currentElement.getStop() <= base.getStop()
                        && currentElement.getStartIndex() % 3 == base.getStartIndex() % 3 ){
                        base.update(currentElement);
                        setIterator.remove();
                    } else {
                        base = currentElement;
                    }
                }
            }

            for (int i = 0; i < locations.size(); i++) {
                locations.get(i).setName("q" + i);
            }
            return locations;

        } catch (TranslationTableParsingException e) {
            throw new LocationGeneratorException(
                    "Error parsing translation table file", e);
        } catch (MascotParsingException e) {
            throw new LocationGeneratorException("Error parsing mascot file", e);
        } catch (GenomeFileParsingException e) {
            throw new LocationGeneratorException("Error parsing genome file", e);
        } catch (IOException e) {
            throw new LocationGeneratorException("Error parsing genome file", e);
        } catch (FastaParserException e) {
            throw new LocationGeneratorException("Error parsing sequence file", e);
        }

    }

    public List<ProteinLocation> doGenerateLocations()
            throws GenomeFileParsingException, MascotParsingException,
            IOException, TranslationTableParsingException, FastaParserException {
        List<ProteinLocation> proteinLocations = new ArrayList<ProteinLocation>();
        System.out.println("Generating locations");
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        genome = genomeParser.parseGenomeFile(genomeFile);

        proteinToOLNMap = new EqualProteinOLNMap();

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(
                proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser
                .parseResults(searchResultsPaths);
        translationTable = CodonTranslationTable
                .parseTableFile(translationTableFile);

        for (PeptideSearchResult peptideSearchResult : peptideSearchResults) {
            GeneInfo geneInfo = genome.getGene(peptideSearchResult
                    .getProteinName());
            if (geneInfo == null) {
                System.err.println(peptideSearchResult.getProteinName()
                        + " not found in the genome");
                continue;
            }
            if (peptideFilter != null && (!peptideFilter.accepts(peptideSearchResult))){
                    continue;
            }

            int virtualGeneStart = geneInfo.getStart() - 1;
            int virtualGeneStop = geneInfo.getStop() - 1;

            int peptideAbsoluteStart;
            int peptideAbsoluteStop;

            int startOffset = (peptideSearchResult.getPeptideStart() - 1) * GenomeConstant.BASES_PER_CODON;
            int stopOffset = (peptideSearchResult.getPeptideStop() - 1) * GenomeConstant.BASES_PER_CODON;

            if (geneInfo.isForward()) {
                peptideAbsoluteStart = virtualGeneStart + startOffset;
                peptideAbsoluteStop = virtualGeneStart + stopOffset;
            } else {
                peptideAbsoluteStart = virtualGeneStop - startOffset;
                peptideAbsoluteStop = virtualGeneStop - stopOffset;
            }

            GenomeNucleotides genomeNucleotides = getGenomeNucleotides(geneInfo.getChromosome());
            int startPosition = searchStart(peptideSearchResult, peptideAbsoluteStart, genomeNucleotides, geneInfo);
            int stopPosition = searchStop(peptideSearchResult, peptideAbsoluteStop, genomeNucleotides, geneInfo, false);
            if (startPosition == NOT_FOUND || stopPosition == NOT_FOUND) {
                continue;
            }
            ProteinLocation loc = new ProteinLocation("?", getStartPosition(startPosition, stopPosition) , Math.abs(stopPosition - startPosition),
                    geneInfo.getDirectionStr(), "0", peptideSearchResult.getConfidenceScore(),
                    peptideSearchResult.getProteinName() + "(" + (virtualGeneStart + 1) + "-" + (virtualGeneStop + 1) + ")", geneInfo.getChromosome());
            loc.setAbsoluteStartStop(getStartPosition(startPosition, stopPosition) + "_" + Math.abs(stopOffset - startOffset));
            loc.setOrigin("VPMerger");
            proteinLocations.add(loc);
        }
        return proteinLocations;
    }

    private int getStartPosition(int startPosition, int stopPosition) {
        if (startPosition > stopPosition) {
            return stopPosition + 2;
        }
        return startPosition + 1;
    }

    private List<ProteinLocation> mergeProteins(List<ProteinLocation> locations) {
        Map<Integer, ProteinLocation> proteinMap = new HashMap<Integer, ProteinLocation>();
        for (ProteinLocation location : locations) {
            ProteinLocation loc = proteinMap.get(location.getStartIndex());
            if (loc == null) {
                proteinMap.put(location.getStartIndex(), location);
            } else {
               loc.update(location);
            }
        }
        ArrayList<ProteinLocation> proteinList = new ArrayList<ProteinLocation>(proteinMap.values());
        return proteinList;
    }
    private List<ProteinLocation> removeDuplicates(List<ProteinLocation> locations) {
        Map<String, ProteinLocation> uniqueLocation = new HashMap<String, ProteinLocation>();
        for (ProteinLocation loc : locations) {
            ProteinLocation propetinOnList = uniqueLocation.get(loc.getAbsoluteStartStop());
            if (propetinOnList == null) {
                uniqueLocation.put(loc.getAbsoluteStartStop(), loc);
            } else {
                if (loc.getStartIndex() < propetinOnList.getStartIndex()) {
                    uniqueLocation.put(loc.getAbsoluteStartStop(), loc);
                }
            }
        }
        return new ArrayList<ProteinLocation>(uniqueLocation.values());
    }

    private int searchStop(PeptideSearchResult peptideSearchResult, int peptideAbsoluteStart, GenomeNucleotides genomeNucleotides, GeneInfo geneInfo, boolean reverse) {
        boolean reachedStop = false;
        int endIterator = peptideAbsoluteStart;

        int searchDirection = reverse ? geneInfo.getDirection() * (-1) : geneInfo.getDirection();
        boolean isStopCodon = translationTable.isStopCodon(genomeNucleotides
                .codonAt(endIterator, geneInfo.getDirection()));

        while (!reachedStop && !isStopCodon) {
            endIterator += incrementPosition(searchDirection);
            reachedStop = reachedEdge(endIterator, searchDirection, genomeNucleotides.getSize());
            if (!reachedStop) {
                isStopCodon = translationTable.isStopCodon(genomeNucleotides.codonAt(endIterator, geneInfo.getDirection()));
            }
        }

        if (reachedStop && !isStopCodon) {
            LOG.warn("Reached end of sequence without finding stop codon for peptide " + peptideSearchResult.getPeptideSequence());
            return endIterator -= incrementPosition(searchDirection);
        } 
        
        if (isStopOnEdge(endIterator, genomeNucleotides)) {
            return endIterator;
        }

        return endIterator += incrementPosition(searchDirection);
    }

    private int searchStart(PeptideSearchResult peptideSearchResult, int peptideAbsoluteStart, GenomeNucleotides genomeNucleotides, GeneInfo geneInfo) {
        //find last stop and search for start from there ..
        int previousStop = searchStop(peptideSearchResult, peptideAbsoluteStart, genomeNucleotides, geneInfo, true);
        boolean reachedStart = false;
        int startIterator = previousStop;
        int direction = geneInfo.getDirection();
        boolean isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator, direction));

        while (!reachedStart && !isStartCodon) {
            startIterator += incrementPosition(geneInfo.getDirection());
            reachedStart = reachedEdge(startIterator, geneInfo.getDirection(), genomeNucleotides.getSize()) || reachedPeptideStart(startIterator, peptideAbsoluteStart, geneInfo.getDirection());
            if (!reachedStart) {
                isStartCodon = translationTable.isStartCodon(genomeNucleotides.codonAt(startIterator, direction));
            }
        }

        if (reachedStart && !isStartCodon) {
            LOG.error("Reached beginning of sequence without finding start codon for peptide "
                    + peptideSearchResult.getPeptideSequence());
            if (isStopOnEdge(previousStop, genomeNucleotides)) {
                return previousStop;
            } else {
                return previousStop + (geneInfo.getDirection() * GenomeConstant.BASES_PER_CODON);
            }
        }

        return startIterator;
    }
    private boolean reachedPeptideStart(int startIterator, int peptideAbsoluteStart, int direction) {
        if (direction > 0) {
            return startIterator > peptideAbsoluteStart;
        }
        return startIterator < peptideAbsoluteStart;
    }

    private boolean isStopOnEdge(int previousStop, GenomeNucleotides genomeNucleotides) {
        //if (forward) {
        return ((previousStop <= GenomeConstant.BASES_PER_CODON) || (previousStop >= (genomeNucleotides.getSize() - GenomeConstant.BASES_PER_CODON)));
    }

    private boolean reachedEdge(int position, int direction, int size) {
        if (((position - GenomeConstant.BASES_PER_CODON) <= 0) || ((position + GenomeConstant.BASES_PER_CODON) >= size)) {
            return true;
        }
        return false;
    }

    private GenomeNucleotides getGenomeNucleotides(String gene)
            throws FastaParserException, IOException {
        if (!genomeNucleotidesMap.containsKey(gene)) {
            genomeNucleotidesMap.put(gene, new GenomeNucleotides(fastaParser.readCode(gene)));
        }
        return genomeNucleotidesMap.get(gene);
    }

    private int incrementPosition(int direction) {
        return direction * GenomeConstant.BASES_PER_CODON;
    }
}
