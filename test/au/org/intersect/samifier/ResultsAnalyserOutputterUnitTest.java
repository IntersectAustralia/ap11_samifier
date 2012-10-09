package au.org.intersect.samifier;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 4/10/12
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultsAnalyserOutputterUnitTest
{
    @Test
    public void testOutputForTwoExons()
    {
        Genome genome = new Genome();
        GeneInfo geneInfo = new GeneInfo();
        geneInfo.setChromosome("chrI");
        geneInfo.setDirection("+");
        geneInfo.setStart(75900);
        geneInfo.setStop(76400);
        genome.addGene("YAL038W", geneInfo);

        PeptideSearchResult result = new PeptideSearchResult("q21_p1", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"));
        Map<String, String> proteinToOLNMapping = new HashMap<String, String>();
        proteinToOLNMapping.put("KPYK1_YEAST", "YAL038W");

        PeptideSequence sequence = new PeptideSequence("", "24M238N3M", 63, 0, 0, null);

        ResultsAnalyserOutputter outputter = new ResultsAnalyserOutputter(result, proteinToOLNMapping, genome, sequence);
        String expected = "KPYK1_YEAST\tYAL038W\tYAL038W\t25.95\t469\t474\t6\tchrI\t75900\t76400\t+2\t2\t75963-75986:76225-76227";

        assertEquals(expected, outputter.toString());
    }

    @Test
    public void testOutputForOneExon()
    {
        Genome genome = new Genome();
        GeneInfo geneInfo = new GeneInfo();
        geneInfo.setChromosome("chrI");
        geneInfo.setDirection("+");
        geneInfo.setStart(900);
        geneInfo.setStop(920);
        genome.addGene("YAL038W", geneInfo);

        PeptideSearchResult result = new PeptideSearchResult("q21_p1", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"));
        Map<String, String> proteinToOLNMapping = new HashMap<String, String>();
        proteinToOLNMapping.put("KPYK1_YEAST", "YAL038W");

        PeptideSequence sequence = new PeptideSequence("", "23M", 0, 0, 0, null);

        ResultsAnalyserOutputter outputter = new ResultsAnalyserOutputter(result, proteinToOLNMapping, genome, sequence);
        String expected = "KPYK1_YEAST\tYAL038W\tYAL038W\t25.95\t469\t474\t6\tchrI\t900\t920\t+2\t1\t900-922";

        assertEquals(expected, outputter.toString());
    }

}