package au.org.intersect.samifier.domain;

public class VirtualProtein {
    private String geneId;
    private String name;
    private int startOffset;
    private int endOffset;

    public VirtualProtein(String name, int offset, int endOffset, String geneID) {
        this.name = name;
        this.startOffset = offset;
        this.endOffset = endOffset;
        this.geneId = geneID;
    }
    public String getName() {
        return name;
    }
    public int getStartOffset() {
        return startOffset;
    }
    public int getEndOffset() {
        return endOffset;
    }
    public String getGeneId() {
        return geneId;
    }
}
