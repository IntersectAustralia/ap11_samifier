package au.org.intersect.samifier;

public class PeptideSequence
{
    private String nucleotideSequence;
    private String cigarString;
    private int startIndex;

    public PeptideSequence(String nucleotideSequence, String cigarString, int startIndex)
    {
        this.nucleotideSequence = nucleotideSequence;
        this.cigarString = cigarString;
        this.startIndex = startIndex;
    }

    public String getNucleotideSequence()
    {
        return nucleotideSequence;
    }

    public String getCigarString()
    {
        return cigarString;
    }

    public int getStartIndex()
    {
        return startIndex;
    }
}
