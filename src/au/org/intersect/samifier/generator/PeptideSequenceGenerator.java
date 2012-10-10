package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.PeptideSequence;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 4/10/12
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PeptideSequenceGenerator
{
    public PeptideSequence getPeptideSequence(PeptideSearchResult peptideSearchResult) throws PeptideSequenceGeneratorException;

    public List<PeptideSequence> getPeptideSequences(List<PeptideSearchResult> peptideSearchResults) throws PeptideSequenceGeneratorException;

}