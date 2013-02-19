package au.org.intersect.samifier.filter;

import au.org.intersect.samifier.domain.PeptideSearchResult;

public interface PeptideSearchResultFilter {
    /**
     * Implementations of this interface should return true in the
     * peptideSearchResult should be procesed
     *
     * @param peptideSearchResult
     *            the peptide search result to filter
     * @return true if the peptide passes the filter
     */
    boolean accepts(PeptideSearchResult peptideSearchResult);
}
