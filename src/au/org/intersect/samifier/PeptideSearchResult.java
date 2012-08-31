package au.org.intersect.samifier;

public class PeptideSearchResult
{
    private String id;
    private String peptideSequence;
    private String proteinName;
    private int peptideStart;
    private int peptideStop;

    public PeptideSearchResult(String id, String peptideSequence, String proteinName, int peptideStart, int peptideStop)
    {
        this.id              = id;
        this.peptideSequence = peptideSequence;
        this.proteinName     = proteinName;
        this.peptideStart    = peptideStart;
        this.peptideStop     = peptideStop;
    }

    public String getId()
    {
        return id;
    }

    public String getPeptideSequence()
    {
        return peptideSequence;
    }

    public String getProteinName()
    {
        return proteinName;
    }

    public int getPeptideStart()
    {
        return peptideStart;
    }

    public int getPeptideStop()
    {
        return peptideStop;
    }
}
