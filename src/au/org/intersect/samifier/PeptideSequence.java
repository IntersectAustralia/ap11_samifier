package au.org.intersect.samifier;

public class PeptideSequence
{
    private String nucleotideSequence;
    private String cigarString;
    private int startIndex;
    private int bedStartIndex;
    private int bedStopIndex;

    public PeptideSequence(String nucleotideSequence, String cigarString, int startIndex, int bedStartIndex, int bedStopIndex)
    {
        this.nucleotideSequence = nucleotideSequence;
        this.cigarString = cigarString;
        this.startIndex = startIndex;
        this.bedStartIndex = bedStartIndex;
        this.bedStopIndex = bedStopIndex;
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

    public int getBedStopIndex()
    {
        return bedStopIndex;
    }

    public int getBedStartIndex()
    {
        return bedStartIndex;
    }

    public String toString()
    {
        return "startIndex = " + startIndex + ", "
             + "nucleotideSequence = " + nucleotideSequence + ", "
             + "cigarString = " + cigarString;
    }
}
