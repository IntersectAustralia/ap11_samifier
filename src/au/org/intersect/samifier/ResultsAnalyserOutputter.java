package au.org.intersect.samifier;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 4/10/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultsAnalyserOutputter
{
    private static final String SEPARATOR = "\t";

    private String proteinId;
    private String locusName;
    private String geneId;
    private String score;
    private String startPosition;
    private String stopPosition;
    private String lengthInAminoacids;
    private String chromosomeId;
    private String geneStart;
    private String geneEnd;
    private String frame;
    private String exons;
    private String exonString;

    public ResultsAnalyserOutputter(PeptideSearchResult peptideSearchResult, Map<String, String> proteinToOLNMap, Genome genome, PeptideSequence peptideSequence)
    {
        this.proteinId = peptideSearchResult.getProteinName();
        this.locusName = proteinToOLNMap.get(peptideSearchResult.getProteinName());
        // This one needs to be confirmed
        this.geneId = proteinToOLNMap.get(peptideSearchResult.getProteinName());
        this.score = peptideSearchResult.getConfidenceScore().toString();
        this.startPosition = Integer.toString(peptideSearchResult.getPeptideStart());
        this.stopPosition = Integer.toString(peptideSearchResult.getPeptideStop());
        this.lengthInAminoacids = Integer.toString(peptideSearchResult.getSequenceLength());

        GeneInfo geneInfo = genome.getGene(locusName);

        this.chromosomeId = geneInfo.getChromosome();
        this.geneStart = new Integer(geneInfo.getStart()).toString();
        this.geneEnd = new Integer(geneInfo.getStop()).toString();
        this.frame = geneInfo.getDirection();
        this.exons = new Integer(numberOfExons(peptideSequence.getCigarString())).toString();;
        this.exonString = "";
    }

    private int numberOfExons(String cigarString)
    {
        return cigarString.split("N").length;
    }

    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append(proteinId + SEPARATOR);
        output.append(locusName + SEPARATOR);
        output.append(geneId + SEPARATOR);
        output.append(score + SEPARATOR);
        output.append(startPosition + SEPARATOR);
        output.append(stopPosition + SEPARATOR);
        output.append(lengthInAminoacids + SEPARATOR);
        output.append(chromosomeId + SEPARATOR);
        output.append(geneStart + SEPARATOR);
        output.append(geneEnd + SEPARATOR);
        output.append(frame + SEPARATOR);
        output.append(exons + SEPARATOR);
        output.append(exonString);
        return output.toString();
    }
}
