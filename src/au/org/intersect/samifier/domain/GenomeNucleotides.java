package au.org.intersect.samifier.domain;

import java.io.*;

public class GenomeNucleotides {
    private File geneFile;
    private StringBuffer genomeNucleotides = new StringBuffer();
    private int size;

    public GenomeNucleotides(File geneFile) throws IOException {
        this.geneFile = geneFile;
        readSequence();
    }

    public char nucleotideAt(int position) {
        return genomeNucleotides.charAt(position);
    }

    public String codonAt(int position, int direction) {
        int increment = direction;

        StringBuilder codon = new StringBuilder();
        codon.append(nucleotideAt(position));
        codon.append(nucleotideAt(position + increment));
        codon.append(nucleotideAt(position + 2 * increment));
        return codon.toString();
    }

    private void readSequence() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(geneFile));
        try {
            reader.readLine(); // Skip the header
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\r", "").replace("\n", "");
                genomeNucleotides.append(line);
            }
        } finally {
            reader.close();
        }
    }

    public int getSize() {
        return genomeNucleotides.length();
    }
}
