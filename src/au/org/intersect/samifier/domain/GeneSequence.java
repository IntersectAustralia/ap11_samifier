package au.org.intersect.samifier.domain;

import static au.org.intersect.samifier.parser.GenomeParser.STRAND_PART;
import static au.org.intersect.samifier.parser.GenomeParser.TYPE_PART;
import static au.org.intersect.samifier.parser.GenomeParser.START_PART;
import static au.org.intersect.samifier.parser.GenomeParser.STOP_PART;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GeneSequence
{

    public static final String CODING_SEQUENCE = "CDS";
    public static final String INTRON = "intron";
    public static final Pattern SEQUENCE_RE = Pattern.compile("("+CODING_SEQUENCE+"|"+INTRON+")");

    private String parentId;
    private boolean codingSequence;
    private int start;
    private int stop;
    private int direction;

    public GeneSequence(String parentId, boolean codingSequence, int start, int stop, int direction)
    {
        setParentId(parentId);
        setSequenceType(codingSequence);
        setStart(start);
        setStop(stop);
        setDirection(direction);
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setSequenceType(boolean codingSequence)
    {
        this.codingSequence = codingSequence;
    }

    public boolean getSequenceType()
    {
        return codingSequence;
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

    public void setDirection(int direction)
    {
        this.direction = direction;
    }

    public int getDirection()
    {
        return direction;
    }

}
