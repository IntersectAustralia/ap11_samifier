package au.org.intersect.samifier.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import au.org.intersect.samifier.domain.BedLineOutputter;
import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.DebuggingFlag;
import au.org.intersect.samifier.domain.Genome;
import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.PeptideSequence;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import au.org.intersect.samifier.domain.SAMEntry;
import au.org.intersect.samifier.domain.SAMEntryComparator;
import au.org.intersect.samifier.filter.ConfidenceScoreFilter;
import au.org.intersect.samifier.filter.PeptideSearchResultFilter;
import au.org.intersect.samifier.generator.PeptideSequenceGenerator;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorException;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import au.org.intersect.samifier.parser.FastaParserException;
import au.org.intersect.samifier.parser.GenomeParserImpl;
import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;

public class SamifierRunner {

    private static Logger LOG = Logger.getLogger(SamifierRunner.class);

    // Command line options
    private String[] searchResultsPaths;
    private File genomeFile;
    private File mapFile;
    private File chromosomeDir;
    private File outfile;
    private String bedfilePath;
    private BigDecimal confidenceScore;

    private Genome genome;
    private ProteinToOLNMap proteinToOLNMap;
    private File translationTableFile;

    public SamifierRunner(String[] searchResultsPaths, File genomeFile,
            File mapFile, File chromosomeDir, File outfile, String bedfilePath,
            BigDecimal confidenceScore, File translationTableFile)
            throws Exception {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.mapFile = mapFile;
        this.chromosomeDir = chromosomeDir;
        this.outfile = outfile;
        this.bedfilePath = bedfilePath;
        this.confidenceScore = confidenceScore;
        this.translationTableFile = translationTableFile;
    }

    public SamifierRunner(String[] searchResultsPaths, File genomeFile,
            File mapFile, File chromosomeDir, File outfile, String bedfilePath,
            BigDecimal confidenceScore) {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.mapFile = mapFile;
        this.chromosomeDir = chromosomeDir;
        this.outfile = outfile;
        this.bedfilePath = bedfilePath;
        this.confidenceScore = confidenceScore;
    }

    public void run() throws Exception {
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        genome = genomeParser.parseGenomeFile(genomeFile);

        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);

        FileWriter bedWriter = null;
        if (bedfilePath != null) {
            bedWriter = new FileWriter(bedfilePath);
        }
        FileWriter sam = new FileWriter(outfile);
        // sort search result by proteins
        peptideSearchResults = peptideSearchResultsParser.sortResultsByChromosome(peptideSearchResults, proteinToOLNMap, genome);
        createSAM(peptideSearchResults, sam, bedWriter);
    }

    public void createSAM(List<PeptideSearchResult> peptideSearchResults,
            Writer output, Writer bedWriter)
            throws PeptideSequenceGeneratorException, IOException, FastaParserException {
        LOG.debug("creating sam file");
        List<SAMEntry> samEntries = new ArrayList<SAMEntry>();
        PeptideSequenceGenerator sequenceGenerator = new PeptideSequenceGeneratorImpl(
                genome, proteinToOLNMap, chromosomeDir);
        Set<String> foundProteins = new HashSet<String>();

        PeptideSearchResultFilter peptideFilter = null;
        if (confidenceScore != null) {
            peptideFilter = new ConfidenceScoreFilter(confidenceScore);
        }

        for (PeptideSearchResult result : peptideSearchResults) {
            if (peptideFilter != null && !peptideFilter.accepts(result)) {
                continue;
            }

            PeptideSequence peptide = sequenceGenerator.getPeptideSequence(result);
            if (peptide == null) {
                LOG.warn("Error while geting peptide sequnce for " + result.getId());
                continue;
            }
            String proteinName = result.getProteinName();
            String resultName = proteinName + "." + result.getId();
            int peptideStart = peptide.getStartIndex()
                    + peptide.getGeneInfo().getStart();

            if (bedWriter != null && !foundProteins.contains(proteinName)) {
                foundProteins.add(proteinName);
                BedLineOutputter bedLineOutputter = new BedLineOutputter(
                        peptide, proteinName);
                bedWriter.write(bedLineOutputter.toString());
            }

            if (DebuggingFlag.get_use_mascot_score_flag() == 1) {
                int mapq = result.getConfidenceScore()
                        .round(new MathContext(0)).intValue();
                samEntries.add(new SAMEntry(resultName, peptide.getGeneInfo(),
                        peptideStart, peptide.getCigarString(), peptide
                                .getNucleotideSequence(), mapq));
            } else {
                int mapq = result.getConfidenceScore().round(new MathContext(0)).intValue();
                String sequnece = peptide.getNucleotideSequence();
                String outputSequence = (peptide.getGeneInfo().getDirection() == -1) ? new StringBuilder(StringUtils.replaceChars(sequnece, "ACGT", "TGCA")).reverse().toString() : sequnece;
                SAMEntry entry = new SAMEntry(resultName, peptide.getGeneInfo(), peptideStart, peptide.getCigarString(), outputSequence, mapq);
                entry.setChromosomeLength(sequenceGenerator.getFastaParser().getChromosomeLength(peptide.getGeneInfo().getChromosome()));
                samEntries.add(entry);
            }

            try {
                if (DebuggingFlag.get_sbi_debug_flag() == 1) {
                    CodonTranslationTable translationTable = CodonTranslationTable
                            .parseTableFile(translationTableFile);
                    String nucleotideString = peptide.getNucleotideSequence();
                    int direction = peptide.getGeneInfo().getDirection();
                    String mascotPeptideString = result.getPeptideSequence();
                    String predictedAminoAcidSequence = new String("");

                    if (direction != 1) {
                        StringBuilder invertedReversedSequence = new StringBuilder(
                                StringUtils.replaceChars(nucleotideString,
                                        "ACGT", "TGCA")).reverse();
                        predictedAminoAcidSequence = translationTable
                                .proteinToAminoAcidSequence(invertedReversedSequence
                                        .toString());
                    } else {
                        predictedAminoAcidSequence = translationTable
                                .proteinToAminoAcidSequence(nucleotideString);
                    }

                    if (!predictedAminoAcidSequence.equals(mascotPeptideString)) {
                        String samEntryStrng = new SAMEntry(resultName,
                                peptide.getGeneInfo(), peptideStart,
                                peptide.getCigarString(),
                                peptide.getNucleotideSequence()).toString();

                        LOG.info("Incorrect nucleotide sequence for following SAM entry:\n"
                                + samEntryStrng);
                    }

                }
            } catch (Exception npe) {
                LOG.info("Problem with internal validation of output nucleotide sequence!");
                npe.printStackTrace();

                System.err.println(npe);
            }

        }

        String prevChromosome = null;
        Collections.sort(samEntries, new SAMEntryComparator());

        // ////////////////////////////////////////////////
        // // Location to store SAM file headers ///
        // ////////////////////////////////////////////////
        //reverse if needed
        //writing header
        ArrayList<String> chromosomes = new ArrayList<String>();
        for (SAMEntry samEntry : samEntries) {
            if (chromosomes.size() > 0 && chromosomes.get(chromosomes.size() - 1).equalsIgnoreCase(samEntry.getRname())) {
                continue;
            }
            chromosomes.add(samEntry.getRname());
        }
        output.write("@HD\tVN:1.0\n");
        for (String chromosome : chromosomes) {
            output.write("@SQ\tSN:" + chromosome + "\tLN:" + ((sequenceGenerator.getFastaParser().getChromosomeLength(chromosome)))  + "\n");
        }

        for (SAMEntry samEntry : samEntries) {
            String chromosome = samEntry.getRname();
            if (!chromosome.equals(prevChromosome)) {
                samEntry.setRnext("=");
                prevChromosome = chromosome;
            }
            output.write(samEntry.toString());
        }

        if (bedWriter != null) {
            bedWriter.close();
        }
        output.close();
    }

}
