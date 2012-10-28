package au.org.intersect.samifier.domain;

import java.util.Map;

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

    public ResultsAnalyserOutputter(PeptideSearchResult peptideSearchResult, ProteinToOLNMap proteinToOLNMap, Genome genome, PeptideSequence peptideSequence)
    {
        this.proteinId = peptideSearchResult.getProteinName();
        this.locusName = proteinToOLNMap.getOLN(peptideSearchResult.getProteinName());
        // This one needs to be confirmed
        this.geneId = proteinToOLNMap.getOLN(peptideSearchResult.getProteinName());
        this.score = peptideSearchResult.getConfidenceScore().toString();
        this.startPosition = Integer.toString(peptideSearchResult.getPeptideStart());
        this.stopPosition = Integer.toString(peptideSearchResult.getPeptideStop());
        this.lengthInAminoacids = Integer.toString(peptideSearchResult.getSequenceLength());

        GeneInfo geneInfo = genome.getGene(locusName);

        this.chromosomeId = geneInfo.getChromosome();
        this.geneStart = new Integer(geneInfo.getStart()).toString();
        this.geneEnd = new Integer(geneInfo.getStop()).toString();
        this.frame = getFrame(geneInfo);

        this.exons = new Integer(numberOfExons(peptideSequence.getCigarString())).toString();;
        this.exonString = getExonString(peptideSequence, geneInfo);
    }

    private String getExonString(PeptideSequence peptideSequence, GeneInfo geneInfo)
    {
        // cigar string example: 23M238N4M
        // first 23 are introns
        // then gap of 238
        // then next 4 are introns
        String cigar = peptideSequence.getCigarString();
        String [] sequenceLengths = cigar.split("N|M");
        StringBuilder exonString = new StringBuilder();

        int startPosition = geneInfo.getStart() + peptideSequence.getStartIndex();
        boolean intron = true;
        boolean firstOne = true;

        for (String sequenceLengthStr : sequenceLengths)
        {
            int sequenceLength = Integer.parseInt(sequenceLengthStr);
            int stopPosition = startPosition + sequenceLength - 1;
            if (intron)
            {
                if (firstOne)
                {
                    firstOne = false;
                }
                else
                {
                    exonString.append(":");
                }

                exonString.append(Integer.toString(startPosition));
                exonString.append("-");
                exonString.append(Integer.toString(stopPosition));
            }

            startPosition = stopPosition + 1;
            intron = !intron;
        }
        return exonString.toString();
    }

    private String getFrame(GeneInfo geneInfo)
    {
        String direction = geneInfo.getDirection();
        int offset = (geneInfo.getStart() - 1) % 3;
        return direction + Integer.toString(offset);
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
