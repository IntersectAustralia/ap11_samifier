package au.org.intersect.samifier.domain;

import java.util.Comparator;

public class GeneSequenceComparator implements Comparator<GeneSequence> {
    public int compare(GeneSequence a, GeneSequence b) {
        return a.getStart() - b.getStart();
    }
}
