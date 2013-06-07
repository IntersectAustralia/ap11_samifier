package au.org.intersect.samifier.domain;

public class VirtualProtein {

    private String name;
    private int startOffset;
    private int endOffset;

    public VirtualProtein(String name, int offset, int endOffset) {
        this.name = name;
        this.startOffset = offset;
        this.endOffset = endOffset;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getStartOffset() {
        return startOffset;
    }
    public void setStartOffset(int offset) {
        this.startOffset = offset;
    }
    public int getEndOffset() {
        return endOffset;
    }
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }
}
