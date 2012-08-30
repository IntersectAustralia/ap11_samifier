package au.org.intersect.samifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.File;

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

    public Samifier(Genome genome, Map<String,String> proteinOLNMap) {
        this.genome = genome;
        //this.proteinOLNMap = proteinOLNMap
    }

    public static void main(String[] args) {
    }
}
