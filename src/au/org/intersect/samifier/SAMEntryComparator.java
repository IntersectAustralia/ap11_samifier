package au.org.intersect.samifier;

import java.util.Comparator;

public class SAMEntryComparator implements Comparator<SAMEntry>
{
    public int compare(SAMEntry a, SAMEntry b)
    {
        int rnameComparision = compareRname(a, b);
        if (rnameComparision == 0)
        {
            return comparePos(a, b);
        }
        return rnameComparision;
    }

    private int compareRname(SAMEntry a, SAMEntry b)
    {
        return a.getRname().compareTo(b.getRname());
    }

    private int comparePos(SAMEntry a, SAMEntry b)
    {
        if (a.getPos() > b.getPos())
        {
            return 1;
        }
        else if (a.getPos() < b.getPos())
        {
            return -1;
        }
        return 0;
    }
}


