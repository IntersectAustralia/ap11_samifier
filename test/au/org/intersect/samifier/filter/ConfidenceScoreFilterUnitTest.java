package au.org.intersect.samifier.filter;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ConfidenceScoreFilterUnitTest
{
    ConfidenceScoreFilter filter;

    @Before
    public void oneTimeSetup()
    {
        filter = new ConfidenceScoreFilter(new BigDecimal("10.20"));
    }

    @Test
    public void testConfidenceScoreEqual()
    {
        BigDecimal confidenceScore = new BigDecimal("10.20");
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult(null,null,null,0,0, confidenceScore);

        assertTrue(filter.accepts(peptideSearchResult));
    }


    @Test
    public void testConfidenceScoreGreater()
    {
        BigDecimal confidenceScore = new BigDecimal("11.20");
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult(null,null,null,0,0, confidenceScore);

        assertTrue(filter.accepts(peptideSearchResult));
    }


    @Test
    public void testConfidenceScoreSmaller()
    {
        BigDecimal confidenceScore = new BigDecimal("1.20");
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult(null,null,null,0,0, confidenceScore);

        assertFalse(filter.accepts(peptideSearchResult));
    }
}
