package au.org.intersect.samifier.domain;

import java.util.Comparator;

public class SAMEntryComparator implements Comparator<SAMEntry> {
    public int compare(SAMEntry a, SAMEntry b) {
        int rnameComparision = compareRname(a, b);
        if (rnameComparision == 0) {
            return comparePos(a, b);
        }
        return rnameComparision;
    }

    private int compareRname(SAMEntry a, SAMEntry b) {
        return a.getRname().compareTo(b.getRname());
    }

    private int comparePos(SAMEntry a, SAMEntry b) {
        return a.getPos() - b.getPos();
    }
}
