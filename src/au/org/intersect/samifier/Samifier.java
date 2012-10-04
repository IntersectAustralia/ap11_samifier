package au.org.intersect.samifier;

import au.org.intersect.samifier.mascot.*;
import org.apache.commons.cli.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

public class Samifier {

    private static Logger LOG = Logger.getLogger(Samifier.class);

    public static final int SAM_REVERSE_FLAG = 0x10;

    private Samifier(){}

    public static Map<String,String> parseProteinToOLNMappingFile(File f)
        throws IOException, FileNotFoundException, ProteinToOLNMappingFileParsingException
    {
        Map<String,String> proteinOLN = new HashMap<String,String>();

        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(f));
 
            // Skip header line
            String line = reader.readLine();
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (line.matches("^#.*$"))
                {
                    continue;
                }
                // ordered_locus_name accession_id protein_name id
                // Tab delimited
                String[] parts = line.split("\\s+");
                if (parts.length < 3)
                {
                    throw new ProteinToOLNMappingFileParsingException("Line "+lineNumber+" not in expected format, should be: ordered_locus_name accession_id protein_name id");
                }
                proteinOLN.put(parts[2], parts[0]);
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return proteinOLN;
    }

    public static String createBEDLine(PeptideSequence peptide, String chromosome, String name)
    {
        StringBuilder output = new StringBuilder();
        output.append(chromosome).append("\t");
        output.append(peptide.getBedStartIndex()).append("\t");
        output.append(peptide.getBedStopIndex()).append("\t");
        output.append(name);
        output.append(System.getProperty("line.separator"));

        return output.toString();
    }


    public static void createSAM(Genome genome, Map<String, String> proteinOLNMap, List<PeptideSearchResult> peptideSearchResults, File chromosomeDirectory, Writer output, Writer bedWriter)
            throws PeptideSequenceGeneratorException, IOException
    {
        LOG.debug("creating sam file");
        List<SAMEntry> samEntries = new ArrayList<SAMEntry>();
        PeptideSequenceGenerator sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinOLNMap, chromosomeDirectory);

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

            if (bedWriter != null)
            {
                bedWriter.write(createBEDLine(peptide, peptide.getGeneInfo().getChromosome(), resultName));
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

    private static void setFileLogger(String logFileName)
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


    public static void main(String[] args)
    {
        Option resultsFile = OptionBuilder.withArgName("searchResultsFile")
                                          .hasArgs()
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
        Option chrDirOpt  = OptionBuilder.withArgName("chromosomeDir")
                                          .hasArg()
                                          .withDescription("Directory containing the chromosome files in FASTA format for the given genome")
                                          .isRequired()
                                          .create("c");
        Option logFile = OptionBuilder.withArgName("logFile")
                                          .hasArg()
                                          .withDescription("Filename to write the log into")
                                          .create("r");
        Option outputFile = OptionBuilder.withArgName("outputFile")
                                          .hasArg()
                                          .withDescription("Filename to write the SAM format file to")
                                          .isRequired()
                                          .create("o");
        Option bedFile = OptionBuilder.withArgName("bedFile")
                                          .hasArg()
                                          .withDescription("Filename to write IGV regions of interest (BED) file to")
                                          .isRequired()
                                          .create("b");
        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(chrDirOpt);
        options.addOption(logFile);
        options.addOption(outputFile);
        options.addOption(bedFile);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            String[] searchResultsPaths = line.getOptionValues("r");
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));
            String logFileName = line.getOptionValue("r");
            String bedfilePath = line.getOptionValue("b");

            if (logFileName != null)
            {
                setFileLogger(logFileName);
            }

            Genome genome = Genome.parse(genomeFile);

            Map<String,String> map = parseProteinToOLNMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(map);

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
            Samifier.createSAM(genome, map, peptideSearchResults, chromosomeDir, sam, bedWriter);
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("samifier", options, true);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }

}

