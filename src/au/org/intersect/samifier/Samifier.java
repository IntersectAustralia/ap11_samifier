package au.org.intersect.samifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
                if (parts.length < 4)
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

    public static void main(String[] args) {
    }
}
