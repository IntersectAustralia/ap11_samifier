package au.org.intersect.samifier.domain;

import java.util.List;
import java.util.regex.Pattern;

public class GeneSequence {

    public static final String CODING_SEQUENCE = "CDS";
    public static final String INTRON = "intron";
    public static final Pattern SEQUENCE_RE = Pattern.compile("("
            + CODING_SEQUENCE + "|" + INTRON + ")");

    private String parentId;
    private boolean codingSequence;
    private int start;
    private int stop;
    private int direction;
    private List<VirtualProtein> virtualProteins;

    public GeneSequence(String parentId, boolean codingSequence, int start, int stop, int direction, List<VirtualProtein> virtualProteins) {
        this(parentId, codingSequence, start, stop, direction);
        this.virtualProteins = virtualProteins;
    }
    
    public GeneSequence(String parentId, boolean codingSequence, int start, int stop, int direction) {
        setParentId(parentId);
        setSequenceType(codingSequence);
        setStart(start);
        setStop(stop);
        setDirection(direction);
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setSequenceType(boolean codingSequence) {
        this.codingSequence = codingSequence;
    }

    public boolean getSequenceType() {
        return codingSequence;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public int getStop() {
        return stop;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public List<VirtualProtein> getVirtualProteins() {
        return virtualProteins;
    }

    public void setVirtualProteins(List<VirtualProtein> virtualProteins) {
        this.virtualProteins = virtualProteins;
    }

}
