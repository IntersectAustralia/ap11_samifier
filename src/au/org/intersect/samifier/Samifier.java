package au.org.intersect.samifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import org.apache.commons.lang3.StringUtils;

public class Samifier {

    public static final int SAM_REVERSE_FLAG = 0x10;
    public static final int BASES_PER_CODON = 3;

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

    public static List<PeptideSearchResult> parseMascotPeptideSearchResults(File resultsFile, Map<String,String> proteinOLN)
        throws IOException, FileNotFoundException
    {
        BufferedReader reader = null;
        boolean peptidesSectionStarted = false;
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        try {
            reader = new BufferedReader(new FileReader(resultsFile));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (peptidesSectionStarted)
                {
                    if (line.startsWith("--"))
                    {
                        break;
                    }
                    results.addAll(getProteinsFromQueryLine(line, proteinOLN));
                }
                else if (line.startsWith("Content-Type: application/x-Mascot; name=\"peptides\""))
                {
                    peptidesSectionStarted = true;
                }
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return results;
    }

    public static PeptideSequence getPeptideSequence(PeptideSearchResult peptide, File chromosomeFile, GeneInfo gene)
        throws FileNotFoundException, IOException
    {
        List<NucleotideSequence> sequenceParts = extractSequenceParts(chromosomeFile, gene);
        StringBuilder nucleotideSequence = new StringBuilder();
        StringBuilder cigar = new StringBuilder();

        // Coordinates for the peptide are 1-based, so substract 1 so it
        // can be used with a 0-based string slice.
        int relativeStart = (peptide.getPeptideStart() - 1) * BASES_PER_CODON;
        int relativeStop  = peptide.getPeptideStop() * BASES_PER_CODON - 1;

        int absoluteStartIndex = 0;
        int absoluteStopIndex = 0;
        int readCursor = 0;
        String direction = gene.getDirection();

        /*
         * The chromosome nucleotide sequence contains everything- exons and
         * introns. The peptide positions we are given exclude the introns.
         *
         * Hence, we walk through each sequence part (describe in the genome
         * file), skipping past introns and just counting through exons.
         */
        for (NucleotideSequence part : sequenceParts)
        {
            // Skip introns, but mark them in the cigar string
            if (GeneSequence.INTRON.equals(part.getType()))
            {
                /*
                   We don't start cigar strings with introns.
                   A non-empty cigar string means we've already got part
                   of the peptide's sequence, and this intron is in the middle.
                */
                int size = part.getStopIndex()-part.getStartIndex()+1;
                if (cigar.length() > 0)
                {
                    updateCigar(cigar, size, GeneSequence.INTRON, direction);
                    absoluteStopIndex += size;
                }
                else
                {
                    // This intron is before our absolute start position
                    absoluteStartIndex += size;
                    absoluteStopIndex += size;
                }
                continue;
            }

            int sequenceSize = part.getSequence().length();
            int substringStart = 0;
            int substringEnd   = sequenceSize;

            // If the desired start position is not in this coding sequence,
            // move our cursor past it
            if (relativeStart > (readCursor + sequenceSize - 1))
            {
                readCursor += sequenceSize;
                absoluteStartIndex += sequenceSize;
                absoluteStopIndex += sequenceSize;
                continue;
            }

            // After skipping through, the next part should have the starting
            // position within it. Update the absoluteStartIndex for the last
            // time.
            if (readCursor < relativeStart)
            {
                substringStart = relativeStart - readCursor;
                absoluteStartIndex += substringStart;
                absoluteStopIndex += substringStart;
            }

            // If this part contains the stop position, then this is the last
            // part to process.
            if ((readCursor + sequenceSize) > relativeStop)
            {
                substringEnd = relativeStop - readCursor + 1;
                nucleotideSequence.append(part.getSequence().substring(substringStart, substringEnd));
                int partSize = substringEnd - substringStart;
                absoluteStopIndex += partSize;
                updateCigar(cigar, partSize, GeneSequence.CODING_SEQUENCE, direction);
                break;
            }

            nucleotideSequence.append(part.getSequence().substring(substringStart, substringEnd));
            int partSize = substringEnd - substringStart;
            absoluteStopIndex += partSize;
            updateCigar(cigar, partSize, GeneSequence.CODING_SEQUENCE, direction);
            readCursor = substringEnd;
        }

        String peptideSequence = GeneInfo.REVERSE.equals(direction) ? nucleotideSequence.reverse().toString() : nucleotideSequence.toString();
        int startIndex = GeneInfo.REVERSE.equals(direction) ? (gene.getStop() - gene.getStart() - absoluteStopIndex + 1) : absoluteStartIndex;
        return new PeptideSequence(peptideSequence, cigar.toString(), startIndex);
    }

    private static void updateCigar(StringBuilder cigar, int size, String type, String direction)
    {
        String marker = GeneSequence.INTRON.equals(type) ? "N" : "M";
        if (GeneInfo.REVERSE.equals(direction))
        {
            cigar.insert(0, marker);
            cigar.insert(0, size);
        }
        else
        {
            cigar.append(size);
            cigar.append(marker);
        }
    }

    public static void createSAM(Genome genome, Map<String, String> proteinOLNMap, List<PeptideSearchResult> peptideSearchResults, File chromosomeDirectory, Writer output)
        throws FileNotFoundException, IOException
    {
        List<SAMEntry> samEntries = new ArrayList<SAMEntry>();
        for (PeptideSearchResult result : peptideSearchResults)
        {
            String proteinName = result.getProteinName();
            String oln = proteinOLNMap.get(proteinName);

            GeneInfo gene = genome.getGene(oln);
            if (gene == null)
            {
                // TODO: log to error file
                continue;
            }
 
            File chromosomeFile = new File(chromosomeDirectory, gene.getChromosome() + ".fa");

            PeptideSequence peptide = getPeptideSequence(result, chromosomeFile, gene);

            int peptideStart = peptide.getStartIndex() + gene.getStart();

            SAMEntry sam = new SAMEntry(proteinName+"."+result.getId(), gene.getChromosome(), peptideStart, peptide.getCigarString(), peptide.getNucleotideSequence());
            if (GeneInfo.REVERSE.equals(gene.getDirection()))
            {
                sam.setFlag(sam.getFlag()|SAM_REVERSE_FLAG);
            }

            samEntries.add(sam);
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
            output.write(System.getProperty("line.separator"));
        }
        output.close();
    }

    private static List<PeptideSearchResult> getProteinsFromQueryLine(String line, Map<String,String> proteinOLN)
        throws IOException
    {
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        // Expected format:
        // q21_p1=0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0;"KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1
        Pattern linePattern = Pattern.compile("^(q\\d+_p\\d+)=([^;]+);(.+)$");
        Pattern proteinPartPattern = Pattern.compile("^\"([^\"]+)\":\\d\\:(\\d+)\\:(\\d+)\\:\\d$");
        Matcher lineMatcher = linePattern.matcher(line);

        if (lineMatcher.matches())
        {
            String id = lineMatcher.group(1);
            String peptidePart = lineMatcher.group(2);
            String proteinsPart = lineMatcher.group(3);

            // Expected format:
            // 0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0
            String[] peptideParts = peptidePart.split(",");
            String peptide = peptideParts[4];

            // Expected format:
            // "KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1, ...
            String[] proteins = proteinsPart.split(",");
            for (String proteinPart : proteins)
            {
                // Expected format:
                // "KPYK1_YEAST":0:469:474:1
                Matcher proteinPartMatcher = proteinPartPattern.matcher(proteinPart);
                if (proteinPartMatcher.matches())
                {
                    String protein = proteinPartMatcher.group(1);
                    if (!proteinOLN.containsKey(protein))
                    {
                        // TODO: report to errors file
                        continue;
                    }
                    int start = Integer.parseInt(proteinPartMatcher.group(2));
                    int stop  = Integer.parseInt(proteinPartMatcher.group(3));
                    results.add(new PeptideSearchResult(id, peptide, protein, start, stop));
                }
            }
        }
        return results;
    }

    public static List<NucleotideSequence> extractSequenceParts(File chromosomeFile, GeneInfo gene)
        throws FileNotFoundException, IOException
    {
        if (!chromosomeFile.exists())
        {
            throw new FileNotFoundException(chromosomeFile.getAbsolutePath() + " not found");
        }

        String direction = gene.getDirection();

        List<NucleotideSequence> parts = new ArrayList<NucleotideSequence>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(chromosomeFile));
            // Skip header of FASTA file
            String line = reader.readLine();
 
            int readCursor = 0;
            List<GeneSequence> locations = gene.getLocations();
            for (GeneSequence location : locations)
            {
                int startIndex = location.getStart();
                int stopIndex  = location.getStop();

                if (GeneSequence.INTRON.equals(location.getSequenceType()))
                {
                    parts.add(new NucleotideSequence(null, GeneSequence.INTRON, startIndex, stopIndex));
                    continue;
                }

                StringBuilder sequence = new StringBuilder();

                // Read forward to startIndex
                while (readCursor < startIndex)
                {
                    line = reader.readLine();
                    line = line.replace("\r", "").replace("\n", "");
                    readCursor += line.length();
                }

                // GFF (Genome) files use 1-based indices
                int readStart = (startIndex - 1) % line.length();
                int readStop  = line.length(); 

                // Read in the nucleotide sequence
                while (readCursor < stopIndex)
                {
                    sequence.append(line.substring(readStart, readStop));
                    readStart = 0;
                    line = reader.readLine();
                    line = line.replace("\r", "").replace("\n", "");
                    readCursor += line.length();
                }

                // Get the last piece
                readStop = (stopIndex - 1) % line.length();
                sequence.append(line.substring(readStart, readStop + 1));

                String sequenceString = GeneInfo.REVERSE.equals(direction) ? sequence.reverse().toString() : sequence.toString();
                parts.add(new NucleotideSequence(sequenceString, GeneSequence.CODING_SEQUENCE, startIndex, stopIndex));
            }

        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }

        if (GeneInfo.REVERSE.equals(direction))
        {
            Collections.reverse(parts);
        }

        return parts;
    }

    public static void main(String[] args)
    {
        Option resultsFile = OptionBuilder.withArgName("searchResultsFile")
                                          .hasArg()
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
        Option outputFile = OptionBuilder.withArgName("outputFile")
                                          .hasArg()
                                          .withDescription("Filename to write the SAM format file to")
                                          .isRequired()
                                          .create("o");
        Options options = new Options();
        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(chrDirOpt);
        options.addOption(outputFile);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse( options, args );
            File searchResultsFile = new File(line.getOptionValue("r"));
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            File outfile = new File(line.getOptionValue("o"));

            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = parseProteinToOLNMappingFile(mapFile);
            List<PeptideSearchResult> peptideSearchResults = Samifier.parseMascotPeptideSearchResults(searchResultsFile, map);

            FileWriter sam = new FileWriter(outfile);

            Samifier.createSAM(genome, map, peptideSearchResults, chromosomeDir, sam);
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

