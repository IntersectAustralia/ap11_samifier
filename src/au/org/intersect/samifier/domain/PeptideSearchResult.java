package au.org.intersect.samifier.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class PeptideSearchResult {
    private String fileName;
    private String id;
    private String peptideSequence;
    private String proteinName;
    private int peptideStart;
    private int peptideStop;
    private BigDecimal confidenceScore;

    public PeptideSearchResult(String fileName, String id, String peptideSequence,
            String proteinName, int peptideStart, int peptideStop,
            BigDecimal confidenceScore) {
        this.id = id;
        this.peptideSequence = peptideSequence;
        this.proteinName = proteinName;
        this.peptideStart = peptideStart;
        this.peptideStop = peptideStop;
        this.confidenceScore = confidenceScore;
        this.fileName = fileName;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(peptideSequence)
                .append(proteinName).append(peptideStart).append(peptideStop)
                .append(confidenceScore).toHashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PeptideSearchResult)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PeptideSearchResult rhs = (PeptideSearchResult) obj;
        return new EqualsBuilder().append(id, rhs.id)
                .append(peptideSequence, rhs.peptideSequence)
                .append(proteinName, rhs.proteinName)
                .append(peptideStart, rhs.peptideStart)
                .append(peptideStop, rhs.peptideStop)
                .append(confidenceScore, rhs.confidenceScore).isEquals();
    }

    public String getId() {
        return id;
    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public String getProteinName() {
        return proteinName;
    }

    public int getPeptideStart() {
        return peptideStart;
    }

    public int getPeptideStop() {
        return peptideStop;
    }

    public int getSequenceLength() {
        return peptideSequence.length();
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public String toString() {
        return "id    = " + id + System.getProperty("line.separator")
                + "name  = " + proteinName
                + System.getProperty("line.separator") + "start = "
                + peptideStart + System.getProperty("line.separator")
                + "stop  = " + peptideStop
                + System.getProperty("line.separator") + "score = "
                + confidenceScore + System.getProperty("line.separator")
                + "sequence = " + System.getProperty("line.separator")
                + peptideSequence;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
