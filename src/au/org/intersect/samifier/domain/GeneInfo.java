package au.org.intersect.samifier.domain;

import au.org.intersect.samifier.Samifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneInfo
{
    private String chromosome;
    private int start;
    private int stop;
    private String direction;
    private List<GeneSequence> locations;

    public GeneInfo()
    {
        //locations = new TreeSet(new GeneSequenceComparator());
        locations = new ArrayList<GeneSequence>();
    }

    public GeneInfo(String chromosome, int start, int stop, String direction)
    {
        //this(chromosome, start, direction, new TreeSet(new GeneSequenceComparator()));
        this(chromosome, start, stop, direction, new ArrayList<GeneSequence>());
    }

    public GeneInfo(String chromosome, int start, int stop, String direction, List<GeneSequence> locations)
    {
        setChromosome(chromosome);
        setStart(start);
        setStop(stop);
        setDirection(direction);
        this.locations = locations;
    }

    public void setChromosome(String chromosome)
    {
        this.chromosome = chromosome;
    }

    public String getChromosome()
    {
        return chromosome;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getStart()
    {
        return start;
    }

    public void setStop(int stop)
    {
        this.stop = stop;
    }

    public int getStop()
    {
        return stop;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public String getDirection()
    {
        return direction;
    }

    public int getDirectionFlag()
    {
        if (GenomeConstant.REVERSE_FLAG.equals(getDirection()))
        {
            return Samifier.SAM_REVERSE_FLAG;
        }
        else
        {
            return 0;
        }
    }

    public void addLocation(GeneSequence location)
    {
        locations.add(location);
    }

    public List<GeneSequence> getLocations()
    {
        Collections.sort(locations, new GeneSequenceComparator());
        return locations;
    }

    public String toString()
    {
        return "chromosome: "+chromosome+", start: "+start+", direction: "+direction;
    }

    public boolean isForward()
    {
        return GenomeConstant.FORWARD_FLAG.equals(getDirection());
    }
}

