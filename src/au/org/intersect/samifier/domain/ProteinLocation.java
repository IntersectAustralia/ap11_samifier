package au.org.intersect.samifier.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class ProteinLocation implements Comparable<ProteinLocation> {
    private String name;
    private int startIndex;
    private int length;
    private String direction;
    private String chromosome;
    private BigDecimal confidenceScore;

    private String frame;
    private String peptideAbsoluteStartStop;
    private Set<String> virtualProteinNames;
    private String origin;

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame) {
        this(name, startIndex, length, direction, frame, null);
    }

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame, BigDecimal confidenceScore) {
        this(name, startIndex, length, direction, frame, null, null, null);
    }

    public ProteinLocation(String name, int startIndex, int length,
            String direction, String frame, BigDecimal confidenceScore,
            String virtualProteinName, String chromosome) {
        this.name = name;
        this.direction = direction;
        this.frame = frame;
        this.confidenceScore = confidenceScore;
        this.startIndex = startIndex;
        this.length = length;
        this.virtualProteinNames = new HashSet<String>();
        this.chromosome = chromosome;
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
        if (virtualProteinNames.size() > 1) {
            return confidenceScore.divide(new BigDecimal(virtualProteinNames.size()), 4, RoundingMode.HALF_UP);
        }
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
        confidenceScore = confidenceScore.add(other.confidenceScore);
        virtualProteinNames.addAll(other.getVirtualProteinNames());
    }

    public String getChromosome() {
        return chromosome;
    }
    @Override
    public int compareTo(ProteinLocation o) {
        int c;
        c = getChromosome().compareTo(o.getChromosome());
        if (c == 0) {
            c = getStartIndex() - o.getStartIndex();
        } 
        if (c == 0) {
            c = o.getStop() - getStop();
        }
        return c;
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ProteinLocation)) return false;
        return this.toString().equals(other.toString());
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public void setAbsoluteStartStop(String peptideAbsoluteStartStop) {
        this.peptideAbsoluteStartStop = peptideAbsoluteStartStop;
    }
    
    public String getAbsoluteStartStop() {
        return peptideAbsoluteStartStop;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

}
