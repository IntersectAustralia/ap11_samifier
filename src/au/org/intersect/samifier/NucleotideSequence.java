package au.org.intersect.samifier;

public class NucleotideSequence
{
    public String sequence;
    public String type;
    public int startIndex;
    public int stopIndex;

    public NucleotideSequence(String sequence, String type, int startIndex, int stopIndex)
    {
        this.sequence   = sequence;
        this.type       = type;
        this.startIndex = startIndex;
        this.stopIndex  = stopIndex;
    }

    public String getSequence()
    {
        return sequence;
    }

    public String getType()
    {
        return type;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getStopIndex()
    {
        return stopIndex;
    }
}

