package au.org.intersect.samifier.mascot;

import au.org.intersect.samifier.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 4/10/12
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideSequenceGeneratorImpl implements PeptideSequenceGenerator
{
    private static Logger LOG = Logger.getLogger(PeptideSequenceGeneratorImpl.class);
    public static final int BASES_PER_CODON = 3;

    private Genome genome;
    private Map<String, String> proteinOLNMap;
    private File chromosomeDirectory;

    public PeptideSequenceGeneratorImpl(Genome genome, Map<String, String> proteinOLNMap, File chromosomeDirectory)
    {
        this.genome = genome;
        this.proteinOLNMap = proteinOLNMap;
        this.chromosomeDirectory = chromosomeDirectory;
    }

    @Override
    public PeptideSequence getPeptideSequence(PeptideSearchResult peptideSearchResult) throws PeptideSequenceGeneratorException
    {
        String proteinName = peptideSearchResult.getProteinName();
        String oln = proteinOLNMap.get(proteinName);

        GeneInfo gene = genome.getGene(oln);
        if (gene == null)
        {
            LOG.info("Protein ID found in accession file, but locus not found in genome file");
            LOG.info("ERR_GFF: " + proteinName + " " + oln);
            return null;
        }

        File chromosomeFile = getChromosomeFile(gene);

        return getPeptideSequenceFromChromosomeFile(peptideSearchResult, chromosomeFile, gene);
    }

    public PeptideSequence getPeptideSequenceFromChromosomeFile(PeptideSearchResult peptide, File chromosomeFile, GeneInfo gene)
            throws PeptideSequenceGeneratorException
    {
        List<NucleotideSequence> sequenceParts;
        try
        {
            sequenceParts = extractSequenceParts(chromosomeFile, gene);
        }
        catch (FileNotFoundException e)
        {
            throw new PeptideSequenceGeneratorException("Chromosome file not found", e);
        }
        catch (IOException e)
        {
            throw new PeptideSequenceGeneratorException("Cannot open chromosome file", e);
        }

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
        // When direction is reverse,
        //  5           17
        // |####----#####|
        //   ^        S
        //   6        15
        //   absoluteStopIndex = 11, from 17 to 6
        //   absoluteStartIndex = 2, from 17 to 15
        //   startIndex  = (17-5 = 12) - 11 + 1 = 2 (because it is 1 based)
        //   stopIndex  = (17-5 = 12) - 2 + 1 = 11 (because it is 1 based)
        int startIndex = GeneInfo.REVERSE.equals(direction) ? (gene.getStop() - gene.getStart() - absoluteStopIndex + 1) : absoluteStartIndex;
        int stopIndex = GeneInfo.REVERSE.equals(direction) ? (gene.getStop() - gene.getStart() - absoluteStartIndex + 1) : absoluteStopIndex;
        int bedStartIndex = gene.getStart() + startIndex - 1; // BED files are zero based (6 in the example)
        int bedStopIndex  = gene.getStart() + stopIndex - 1; // BED files are zero based (15 in the example)
        return new PeptideSequence(peptideSequence, cigar.toString(), startIndex, bedStartIndex, bedStopIndex, gene);
    }

    private void updateCigar(StringBuilder cigar, int size, String type, String direction)
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

    protected List<NucleotideSequence> extractSequenceParts(File chromosomeFile, GeneInfo gene)
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

    private File getChromosomeFile(GeneInfo gene) {
        // TODO: find the different chrormosome file extensions
        return new File(chromosomeDirectory, gene.getChromosome() + ".fa");
    }


}
