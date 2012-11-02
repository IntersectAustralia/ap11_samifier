package au.org.intersect.samifier.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class CodonTranslationTable
{
    public static final String UNKNOWN_AMINO_ACID = "X";

    private Map<String, String> codonMap;
    private Map<String, String> startCodonMap;
    private Set<String> stopCodons;

    private CodonTranslationTable(){}

    public static CodonTranslationTable parseTableFile(File f)
            throws IOException, FileNotFoundException, TranslationTableParsingException
    {
        CodonTranslationTable codonTable = new CodonTranslationTable();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(f));

        String aminoAcids;
        String startAminoAcids;
        String base1, base2, base3;

        try {
            String line = reader.readLine();

            while (!line.matches("^\\s*AAs.*"))
            {
                line = reader.readLine();
            }
            if (line == null)
            {
                throw new TranslationTableParsingException("Supplied translation table is not in the expected format");
            }

            aminoAcids = parseLine(StringUtils.chomp(line));
            startAminoAcids = parseLine(StringUtils.chomp(reader.readLine()));
            base1 = parseLine(StringUtils.chomp(reader.readLine()));
            base2 = parseLine(StringUtils.chomp(reader.readLine()));
            base3 = parseLine(StringUtils.chomp(reader.readLine()));
        }
        catch (NullPointerException npe)
        {
            throw new TranslationTableParsingException("Supplied translation table is not in the expected format");
        }

        codonTable.codonMap = new HashMap<String, String>();
        codonTable.startCodonMap = new HashMap<String, String>();
        codonTable.stopCodons = new HashSet<String>();

        int length = aminoAcids.length();
        for (int i=0; i < length; i++)
        {
            String aminoAcid = aminoAcids.substring(i, i+1);
            String codon = base1.substring(i, i+1)
                    + base2.substring(i, i+1)
                    + base3.substring(i, i+1);
            if (aminoAcid.equals("*"))
            {
                codonTable.stopCodons.add(codon);
                continue;
            }
            String startAminoAcid = startAminoAcids.substring(i, i+1);

            codonTable.codonMap.put(codon, aminoAcid);
            if (startAminoAcid.matches("^[A-Z]$"))
            {
                codonTable.startCodonMap.put(codon, startAminoAcid);
            }
        }

        return codonTable;
    }

    public String[] getCodons()
    {
        return codonMap.keySet().toArray(new String[0]);
    }

    public String[] getStartCodons()
    {
        return startCodonMap.keySet().toArray(new String[0]);
    }

    public String[] getStopCodons()
    {
        return stopCodons.toArray(new String[0]);
    }

    public String toAminoAcid(String codon)
    {
        if (codonMap.containsKey(codon))
        {
            return codonMap.get(codon);
        }
        else if (codon.matches(".*[WSMKRY]+.*"))
        {
            // TODO: log this event
            return UNKNOWN_AMINO_ACID;
        }
        return null;
    }

    public String toStartAminoAcid(String codon)
    {
        return startCodonMap.get(codon);
    }

    public String proteinToAminoAcidSequence(String nucleotideSequence)
            throws UnknownCodonException
    {
        int length = nucleotideSequence.length();
        if (length < 3)
        {
            throw new UnknownCodonException(nucleotideSequence + " is not a known codon");
        }
        StringBuilder aminoAcidSequence = new StringBuilder();
        String codon;
        int codonCount = 0;
        int startIndex = 0;

        // Start codon may be different
        String startCodon = nucleotideSequence.substring(0,3).toUpperCase();
        if (startCodonMap.containsKey(startCodon))
        {
            aminoAcidSequence.append(toStartAminoAcid(startCodon));
            startIndex = 3;
        }

        for (int i=startIndex; i < length; i+=3)
        {
            if ((i+3) > length)
            {
                // TODO: log to error file about sequence length being
                // non-multiple of 3 (i.e. this is not a full codon)
                throw new UnknownCodonException(nucleotideSequence.substring(i, length) + " is not a known codon (at codon "+codonCount+")");
            }
            codon = nucleotideSequence.substring(i, i+3).toUpperCase();
            codonCount++;
            if (stopCodons.contains(codon))
            {
                aminoAcidSequence.append("*");
                continue;
            }
            String aminoAcid = toAminoAcid(codon);
            if (aminoAcid == null)
            {
                throw new UnknownCodonException(codon + " is not a known codon (at codon #"+codonCount+")");
            }

            aminoAcidSequence.append(aminoAcid);
        }

        return aminoAcidSequence.toString();
    }

    private static String parseLine(String line)
    {
        String[] parts = line.split("\\s*=\\s+");
        return parts[1].toUpperCase();
    }

    public boolean isStartCodon(String codon)
    {
        return startCodonMap.keySet().contains(codon);
    }

    public boolean isStopCodon(String codon)
    {
        return stopCodons.contains(codon);
    }

}
