package au.org.intersect.samifier.domain;

import java.math.BigDecimal;

public class ProteinLocation implements Comparable<ProteinLocation> {
    private String name;
    private int startIndex;
    private int length;
    private String direction;

    private BigDecimal confidenceScore;

    private String frame;

    private String virtualProteinName;

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame) {
        this(name, startIndex, length, direction, frame, null);
    }

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame, BigDecimal confidenceScore) {
        this(name, startIndex, length, direction, frame, null, null);
    }

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame, BigDecimal confidenceScore,
            String virtualProteinName) {
        this.name = name;
        this.direction = direction;
        this.frame = frame;
        this.confidenceScore = confidenceScore;
        this.startIndex = startIndex;
        this.length = length;
        this.virtualProteinName = virtualProteinName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getLength() {
        return length;
    }

    public int getStop() {
        return startIndex + length - 1;
    }

    public String getDirection() {
        return direction;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public String getFrame() {
        return frame;
    }

    public String getVirtualProteinName() {
        return virtualProteinName;
    }

    public String toString() {
        return name + ", startIndex=" + startIndex + ", length=" + length
                + ", direction=" + direction;
    }

    @Override
    public int compareTo(ProteinLocation o) {
        return getStartIndex() - o.getStartIndex();
    }

}
