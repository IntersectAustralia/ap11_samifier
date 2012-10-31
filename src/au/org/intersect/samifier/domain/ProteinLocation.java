package au.org.intersect.samifier.domain;

import java.math.BigDecimal;

public class ProteinLocation
{
    public static final String FORWARD = "+";
    public static final String REVERSE = "-";

    private String name;
    private int startIndex;
    private int length;
    private String direction;

    private BigDecimal confidenceScore;

    private String frame;

    public ProteinLocation(String name, int startIndex, int length, String direction, String frame)
    {
        this.name = name;
        this.startIndex = startIndex;
        this.length = length;
        this.direction = direction;
        this.frame = frame;
        this.confidenceScore = null;
    }

    public ProteinLocation(String name, int startIndex, int length, String direction, String frame, BigDecimal confidenceScore)
    {
        this.name = name;
        this.startIndex = startIndex;
        this.length = length;
        this.direction = direction;
        this.frame = frame;
        this.confidenceScore = confidenceScore;
    }

    public String getName()
    {
        return name;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getLength()
    {
        return length;
    }

    public int getStop()
    {
        return startIndex + length;
    }

    public String getDirection()
    {
        return direction;
    }

    public BigDecimal getConfidenceScore()
    {
        return confidenceScore;
    }

    public String getFrame()
    {
        return frame;
    }

    public String toString()
    {
        return name+", startIndex="+startIndex+", length="+length+", direction="+direction;
    }

}
