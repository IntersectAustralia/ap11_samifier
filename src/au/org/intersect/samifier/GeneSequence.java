package au.org.intersect.samifier;

import java.util.List;
import java.util.regex.Pattern;

public class GeneSequence
{

    public static final String GENE = "gene";
    public static final String CODING_SEQUENCE = "CDS";
    public static final String INTRON = "intron";
    public static final Pattern SEQUENCE_RE =
        Pattern.compile("(" + GENE + "|" + CODING_SEQUENCE + "|" + INTRON +")");

    private String sequenceType;
    private int start;
    private int stop;
    private String direction;

    public GeneSequence(String sequenceType, int start, int stop, String direction)
    {
        setSequenceType(sequenceType);
        setStart(start);
        setStop(stop);
        setDirection(direction);
    }

    public void setSequenceType(String sequenceType)
    {
        this.sequenceType = sequenceType;
    }

    public String getSequenceType()
    {
        return sequenceType;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getStart()
    {
        return start;
    }

    public void setStop(int stop)
    {
        this.stop = stop;
    }

    public int getStop()
    {
        return stop;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public String getDirection()
    {
        return direction;
    }

}
