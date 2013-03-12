package au.org.intersect.samifier.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class ProteinLocation implements Comparable<ProteinLocation> {
    private String name;
    private int startIndex;
    private int length;
    private String direction;

    private BigDecimal confidenceScore;

    private String frame;

    private Set<String> virtualProteinNames;

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
        this.virtualProteinNames = new HashSet<String>();
        if (virtualProteinName != null) {
            virtualProteinNames.add(virtualProteinName);
        }
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

    public Set<String> getVirtualProteinNames() {
        return virtualProteinNames;
    }

    public String toString() {
        return name + ", startIndex=" + startIndex + ", length=" + length
                + ", direction=" + direction;
    }
    
    public void update(ProteinLocation other) {
        if (other.getStartIndex() < getStartIndex()) {
            startIndex = other.getStartIndex();
        }
        virtualProteinNames.addAll(other.getVirtualProteinNames());
    }
    
    @Override
    public int compareTo(ProteinLocation o) {
        return getStartIndex() - o.getStartIndex();
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof ProteinLocation) ) return false;
        ProteinLocation otherLocation = (ProteinLocation) other;
        return this.toString().equals(other.toString());
    }

}
