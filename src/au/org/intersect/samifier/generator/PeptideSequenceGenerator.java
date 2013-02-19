package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.PeptideSequence;

import java.util.List;

public interface PeptideSequenceGenerator {
    PeptideSequence getPeptideSequence(PeptideSearchResult peptideSearchResult)
            throws PeptideSequenceGeneratorException;

    List<PeptideSequence> getPeptideSequences(
            List<PeptideSearchResult> peptideSearchResults)
            throws PeptideSequenceGeneratorException;

}
