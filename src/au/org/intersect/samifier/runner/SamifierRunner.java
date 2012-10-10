package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.*;
import au.org.intersect.samifier.generator.PeptideSequenceGenerator;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorException;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import au.org.intersect.samifier.outputter.BedLineOutputter;
import au.org.intersect.samifier.parser.*;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

public class SamifierRunner {

    private static Logger LOG = Logger.getLogger(SamifierRunner.class);

    // Command line options
    private String[] searchResultsPaths;
    private File genomeFile;
    private File mapFile;
    private File chromosomeDir;
    private File outfile;
    private String logFileName;
    private String bedfilePath;

    private Genome genome;
    private Map<String,String> proteinToOLNMap;

    public SamifierRunner(String[] searchResultsPaths, File genomeFile, File mapFile, File chromosomeDir, File outfile, String logFileName,String bedfilePath)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.mapFile = mapFile;
        this.chromosomeDir = chromosomeDir;
        this.outfile = outfile;
        this.logFileName = logFileName;
        this.bedfilePath = bedfilePath;
    }

    public void run() throws Exception
    {
        if (logFileName != null)
        {
            setFileLogger(logFileName);
        }

        genome = Genome.parse(genomeFile);

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

        for (PeptideSearchResult result : peptideSearchResults)
        {
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

    private void setFileLogger(String logFileName)
    {
        Logger.getRootLogger().removeAppender("stdout");
        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(logFileName);
        fa.setLayout(new PatternLayout("%d %-5p %c - %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        Logger.getRootLogger().addAppender(fa);
    }
}

