package au.org.intersect.samifier.filter;

import au.org.intersect.samifier.domain.PeptideSearchResult;

import java.math.BigDecimal;

public class ConfidenceScoreFilter implements PeptideSearchResultFilter {
    private BigDecimal scoreThreshold;

    public ConfidenceScoreFilter(BigDecimal scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    @Override
    public boolean accepts(PeptideSearchResult peptideSearchResult) {
        return peptideSearchResult.getConfidenceScore().compareTo(
                scoreThreshold) >= 0;
    }
}
