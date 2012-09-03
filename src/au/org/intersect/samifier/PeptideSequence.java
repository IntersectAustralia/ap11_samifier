package au.org.intersect.samifier;

public class PeptideSequence
{
    private String nucleotideSequence;
    private String cigarString;

    public PeptideSequence(String nucleotideSequence, String cigarString)
    {
        this.nucleotideSequence = nucleotideSequence;
        this.cigarString = cigarString;
    }

    public String getNucleotideSequence()
    {
        return nucleotideSequence;
    }

    public String getCigarString()
    {
        return cigarString;
    }
}
