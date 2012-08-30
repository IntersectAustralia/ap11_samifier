package au.org.intersect.samifier;

import java.util.Comparator;

public class GeneSequenceComparator implements Comparator<GeneSequence>
{
    public int compare(GeneSequence a, GeneSequence b)
    {
        if (a.getStart() > b.getStart())
        {
            return 1;
        }
        else if (a.getStart() < b.getStart())
        {
            return -1;
        }
        return 0;
    }

}


