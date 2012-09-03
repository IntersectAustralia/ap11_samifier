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

public class Samifier {
    // Initialised with double brace initialisation
    // See: http://www.c2.com/cgi/wiki?DoubleBraceInitialization
    public static final Map<String, String> CODONS = Collections.unmodifiableMap(
        new HashMap<String, String>() {{
            put("ATT", "I");
            put("ATT", "I");
            put("ATC", "I");
            put("ATA", "I");

            put("CTT", "L");
            put("CTC", "L");
            put("CTA", "L");
            put("CTG", "L");
            put("TTA", "L");
            put("TTG", "L");

            put("GTT", "V");
            put("GTC", "V");
            put("GTA", "V");
            put("GTG", "V");

            put("TTT", "F");
            put("TTC", "F");

            put("ATG", "M"); // Also the start codon

            put("TGT", "C");
            put("TGC", "C");

            put("GCT", "A");
            put("GCC", "A");
            put("GCA", "A");
            put("GCG", "A");

            put("CCT", "P");
            put("CCC", "P");
            put("CCA", "P");
            put("CCG", "P");

            put("ACT", "T");
            put("ACC", "T");
            put("ACA", "T");
            put("ACG", "T");

            put("TCT", "S");
            put("TCC", "S");
            put("TCA", "S");
            put("TCG", "S");
            put("AGT", "S");
            put("AGC", "S");

            put("TAT", "Y");
            put("TAC", "Y");

            put("TGG", "W");

            put("CAA", "Q");
            put("CAG", "Q");

            put("AAT", "N");
            put("AAC", "N");

            put("CAT", "H");
            put("CAC", "H");

            put("GAA", "E");
            put("GAG", "E");

            put("GAT", "D");
            put("GAC", "D");

            put("AAA", "K");
            put("AAG", "K");

            put("CGT", "R");
            put("CGC", "R");
            put("CGA", "R");
            put("CGG", "R");
            put("AGA", "R");
            put("AGG", "R");
        }}
    );

    public static final String START_CODON = "ATG";

    public static final Set<String> STOP_CODONS = Collections.unmodifiableSet(
        new HashSet<String>() {{
            add("TAA");
            add("TAG");
            add("TGA");
        }}
    );

    private Genome genome;
    private Map<String,String> proteinOLNMap;

    private Samifier(){}

    public Samifier(Genome genome, Map<String,String> proteinOLNMap)
    {
        this.genome = genome;
        //this.proteinOLNMap = proteinOLNMap
    }

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

    public static PeptideSequence getPeptideSequence(PeptideSearchResult peptide, List<NucleotideSequence> sequenceParts)
    {
        StringBuilder nucleotideSequence = new StringBuilder();
        StringBuilder cigar = new StringBuilder();

        int peptideStart = (peptide.getPeptideStart() - 1) * 3;
        int peptideStop  = peptide.getPeptideStop() * 3 - 1;
        int remaining = peptideStop - peptideStart + 1;

        for (NucleotideSequence part : sequenceParts)
        {
            if (GeneSequence.INTRON.equals(part.getType()))
            {
                if (cigar.length() > 0)
                {
                    cigar.append(part.getStopIndex()-part.getStartIndex()+1);
                    cigar.append("N");
                }
                continue;
            }

            int sequenceSize = part.getSequence().length();

            if (peptideStart >= sequenceSize)
            {
                peptideStart -= sequenceSize;
                continue;
            }

            if ((peptideStart + remaining) < sequenceSize)
            {
                nucleotideSequence.append(part.getSequence().substring(peptideStart, peptideStart+remaining));
                cigar.append(remaining);
                cigar.append("M");
                break;
            }

            nucleotideSequence.append(part.getSequence().substring(peptideStart, sequenceSize));
            cigar.append(sequenceSize-peptideStart);
            cigar.append("M");
            remaining -= (sequenceSize - peptideStart);
            peptideStart = 0;

            if (remaining <= 0)
            {
                break;
            }
        }

        return new PeptideSequence(nucleotideSequence.toString(), cigar.toString());
    }

    public static void createSAM(Genome genome, Map<String, String> proteinOLNMap, List<PeptideSearchResult> peptideSearchResults, File chromosomeDirectory, Writer output)
        throws FileNotFoundException, IOException
    {
        List<SAMEntry> samEntries = new ArrayList<SAMEntry>();
        for (PeptideSearchResult result : peptideSearchResults)
        {
            String oln = proteinOLNMap.get(result.getProteinName());
            GeneInfo gene = genome.getGene(oln);
            if (gene == null)
            {
                // TODO: log to error file
                continue;
            }
            // TODO: handle directionality in a future release
            if (!"+".equals(gene.getDirection()))
            {
                continue;
            }
 
            File chromosomeFile = new File(chromosomeDirectory, gene.getChromosome() + ".fa");
            List<NucleotideSequence> sequenceParts = extractSequenceParts(chromosomeFile, gene.getLocations());

            PeptideSequence peptide = getPeptideSequence(result, sequenceParts);

            String proteinName = result.getProteinName();
            int peptideStart = (result.getPeptideStart()-1)*3 + gene.getStart();

            samEntries.add(new SAMEntry(proteinName+"."+result.getId(), gene.getChromosome(), peptideStart, peptide.getCigarString(), peptide.getNucleotideSequence()));
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
        // q21_p1=0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0;"KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1
        Pattern linePattern = Pattern.compile("^(q\\d+_p\\d+)=([^;]+);(.+)$");
        Pattern proteinPartPattern = Pattern.compile("^\"([^\"]+)\":\\d\\:(\\d+)\\:(\\d+)\\:\\d$");
        Matcher lineMatcher = linePattern.matcher(line);
        if (lineMatcher.matches())
        {
            String id = lineMatcher.group(1);
            String peptidePart = lineMatcher.group(2);
            String proteinsPart = lineMatcher.group(3);
            //0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0
            String[] peptideParts = peptidePart.split(",");
            String peptide = peptideParts[4];

            //"KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1, ...
            String[] proteins = proteinsPart.split(",");
            for (String proteinPart : proteins)
            {
                //"KPYK1_YEAST":0:469:474:1
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

    public static List<NucleotideSequence> extractSequenceParts(File chromosomeFile, List<GeneSequence> locations)
        throws FileNotFoundException, IOException
    {

        if (!chromosomeFile.exists())
        {
            throw new FileNotFoundException(chromosomeFile.getAbsolutePath() + " not found");
        }

        List<NucleotideSequence> parts = new ArrayList<NucleotideSequence>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(chromosomeFile));
            // Skip header of FASTA file
            String line = reader.readLine();
 
            int readCursor = 0;
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

                parts.add(new NucleotideSequence(sequence.toString(), GeneSequence.CODING_SEQUENCE, startIndex, stopIndex));
            }

        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }

        return parts;
    }

    public static void main(String[] args)
    {
        try {
            File searchResultsFile = new File(args[0]);
            File genomeFile = new File(args[1]);
            File mapFile = new File(args[2]);
            File chromosomeDir = new File(args[3]);
            File outfile = new File(args[4]);

            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = parseProteinToOLNMappingFile(mapFile);
            List<PeptideSearchResult> peptideSearchResults = Samifier.parseMascotPeptideSearchResults(searchResultsFile, map);

            FileWriter sam = new FileWriter(outfile);

            Samifier.createSAM(genome, map, peptideSearchResults, chromosomeDir, sam);
        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}

