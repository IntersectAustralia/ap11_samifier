package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.PeptideSequence;
import au.org.intersect.samifier.parser.FastaParser;

import java.util.List;

public interface PeptideSequenceGenerator {
    PeptideSequence getPeptideSequence(PeptideSearchResult peptideSearchResult)
            throws PeptideSequenceGeneratorException;

    FastaParser getFastaParser();
}
