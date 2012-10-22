package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.domain.*;
import au.org.intersect.samifier.filter.ConfidenceScoreFilter;
import au.org.intersect.samifier.filter.PeptideSearchResultFilter;
import au.org.intersect.samifier.generator.PeptideSequenceGenerator;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorException;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import au.org.intersect.samifier.parser.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.*;

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
    private Map<String,String> proteinToOLNMap;


    public SamifierRunner(String[] searchResultsPaths, File genomeFile, File mapFile, File chromosomeDir, File outfile, String bedfilePath, BigDecimal confidenceScore)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.mapFile = mapFile;
        this.chromosomeDir = chromosomeDir;
        this.outfile = outfile;
        this.bedfilePath = bedfilePath;
        this.confidenceScore = confidenceScore;
    }

    public void run() throws Exception
    {
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        genome = genomeParser.parseGenomeFile(genomeFile);

        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);

        List<PeptideSearchResult> peptideSearchResults = new ArrayList<PeptideSearchResult>();
        List<File> searchResultFiles = new ArrayList<File>();
        for (String searchResultsPath : searchResultsPaths)
        {
            File searchResultFile = new File(searchResultsPath);
            if (!searchResultFile.exists())
            {
                System.err.println(searchResultFile + " does not exist");
                System.exit(1);
            }
            searchResultFiles.add(searchResultFile);
        }

        for (File searchResultFile : searchResultFiles)
        {
            LOG.debug("Processing: " + searchResultFile.getAbsolutePath());
            peptideSearchResults.addAll(peptideSearchResultsParser.parseResults(searchResultFile));
        }

        FileWriter bedWriter = null;
        if (bedfilePath != null)
        {
            bedWriter = new FileWriter(bedfilePath);
        }
        FileWriter sam = new FileWriter(outfile);
        createSAM(peptideSearchResults, sam, bedWriter);

    }


    public void createSAM(List<PeptideSearchResult> peptideSearchResults, Writer output, Writer bedWriter)
            throws PeptideSequenceGeneratorException, IOException
    {
        LOG.debug("creating sam file");
        List<SAMEntry> samEntries = new ArrayList<SAMEntry>();
        PeptideSequenceGenerator sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinToOLNMap, chromosomeDir);
        Set<String> foundProteins = new HashSet<String>();

        PeptideSearchResultFilter peptideFilter = null;
        if (confidenceScore != null)
        {
            peptideFilter = new ConfidenceScoreFilter(confidenceScore);
        }

        for (PeptideSearchResult result : peptideSearchResults)
        {
            if (peptideFilter != null && !peptideFilter.accepts(result))
            {
                continue;
            }

            PeptideSequence peptide = sequenceGenerator.getPeptideSequence(result);
            if (peptide == null)
            {
                continue;
            }

            String proteinName = result.getProteinName();
            String resultName = proteinName+"."+result.getId();
            int peptideStart = peptide.getStartIndex() + peptide.getGeneInfo().getStart();

            if (bedWriter != null && !foundProteins.contains(proteinName))
            {
                foundProteins.add(proteinName);
                BedLineOutputter bedLineOutputter = new BedLineOutputter(peptide, proteinName);
                bedWriter.write(bedLineOutputter.toString());
            }

            samEntries.add(new SAMEntry(resultName, peptide.getGeneInfo(), peptideStart, peptide.getCigarString(), peptide.getNucleotideSequence()));
        }

        String prevChromosome = null;
        Collections.sort(samEntries, new SAMEntryComparator());
        for (SAMEntry samEntry : samEntries)
        {
            String chromosome = samEntry.getRname();
            if (! chromosome.equals(prevChromosome))
            {
                samEntry.setRnext("=");
                prevChromosome = chromosome;
            }
            output.write(samEntry.toString());
        }

        if (bedWriter != null)
        {
            bedWriter.close();
        }
        output.close();
    }

}

