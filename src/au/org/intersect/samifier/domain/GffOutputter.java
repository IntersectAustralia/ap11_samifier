package au.org.intersect.samifier.domain;

public class GffOutputter implements Outputter
{
    public String genomeFileName;
    public Integer start;
    public Integer end;
    private String glimmerScore;
    private String directionFlag;
    private String frame;
    private String glimmerName;

    public GffOutputter(ProteinLocation location, String genomeFileNameWithExtension)
    {
        this.genomeFileName =  genomeFileNameNoExtension(genomeFileNameWithExtension);
        this.start = location.getStartIndex();
        this.end = location.getStop();
        if (location.getConfidenceScore() != null)
        {
            this.glimmerScore = location.getConfidenceScore().toString();
        }
        else
        {
            this.glimmerScore = "0";
        }
        this.directionFlag = location.getDirection();
        this.frame = location.getFrame();
        this.glimmerName = location.getName();
    }

    @Override
    public String getOutput()
    {
        StringBuilder output = new StringBuilder();

        output.append(getOutputLine("gene"));
        output.append(System.getProperty("line.separator"));

        output.append(getOutputLine("CDS"));
        output.append(System.getProperty("line.separator"));

        return output.toString();

    }

    private String getOutputLine(String type)
    {
        StringBuilder output = new StringBuilder();
        output.append(genomeFileName);
        column(output, "Glimmer");
        column(output, type);
        column(output, Integer.toString(start));
        column(output, Integer.toString(end));
        column(output, glimmerScore);
        column(output, directionFlag);
        column(output, frame);
        column(output, "ID=" + glimmerName + ";Name=" + glimmerName + ";Note=");
        return output.toString();
    }

    private void column(StringBuilder buff, String field)
    {
        buff.append('\t');
        buff.append(field);
    }

    private String genomeFileNameNoExtension(String genomeFileName)
    {
        int indexOfDot = genomeFileName.lastIndexOf('.');
        if (indexOfDot > 0)
        {
            return genomeFileName.substring(0, indexOfDot);
        }
        return genomeFileName;
    }
}
