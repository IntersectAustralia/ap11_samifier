package au.org.intersect.samifier.domain;

public class PeptideSequence {
    private String nucleotideSequence;
    private String cigarString;
    private int startIndex;
    private int bedStartIndex;
    private int bedStopIndex;

    public GeneInfo getGeneInfo() {
        return geneInfo;
    }

    private GeneInfo geneInfo;

    public PeptideSequence(String nucleotideSequence, String cigarString,
            int startIndex, int bedStartIndex, int bedStopIndex,
            GeneInfo geneInfo) {
        this.nucleotideSequence = nucleotideSequence;
        this.cigarString = cigarString;
        this.startIndex = startIndex;
        this.bedStartIndex = bedStartIndex;
        this.bedStopIndex = bedStopIndex;
        this.geneInfo = geneInfo;
    }

    public String getNucleotideSequence() {
        return nucleotideSequence;
    }

    public String getCigarString() {
        return cigarString;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getBedStopIndex() {
        return bedStopIndex;
    }

    public int getBedStartIndex() {
        return bedStartIndex;
    }

    public String toString() {
        return "startIndex = " + startIndex + ", " + "nucleotideSequence = "
                + nucleotideSequence + ", " + "cigarString = " + cigarString;
    }
}
