package au.org.intersect.samifier.domain;

import org.apache.commons.lang3.StringUtils;


public class GenomeNucleotides {
    private String genomeNucleotides;


    public GenomeNucleotides(String genomeNucleotides) {
        this.genomeNucleotides = genomeNucleotides;
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
        return direction  > 0 ? codon.toString() : invertNucleotideSequence(codon.toString());
    }


    public int getSize() {
        return genomeNucleotides.length();
    }

    private String invertNucleotideSequence(String sequence) {
        return StringUtils.replaceChars(sequence, "ACGT", "TGCA");
    }
}
