package au.org.intersect.samifier.domain;

import au.org.intersect.samifier.parser.GenomeParserImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultsAnalyserOutputterUnitTest
{
    @Test
    public void testOutputForTwoExons()
    {
        Genome genome = new Genome();
        GeneInfo geneInfo = new GeneInfo();
        geneInfo.setId("YAL038W");
        geneInfo.setChromosome("chrI");
        geneInfo.setDirection(1);
        geneInfo.setStart(75900);
        geneInfo.setStop(76400);
        genome.addGene(geneInfo);

        PeptideSearchResult result = new PeptideSearchResult("q21_p1", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"));
        FileBasedProteinToOLNMap proteinToOLNMapping = new FileBasedProteinToOLNMap();
        proteinToOLNMapping.addMapping("KPYK1_YEAST", "YAL038W");

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
        geneInfo.setId("YAL038W");
        geneInfo.setChromosome("chrI");
        geneInfo.setDirection(1);
        geneInfo.setStart(900);
        geneInfo.setStop(920);
        genome.addGene(geneInfo);

        PeptideSearchResult result = new PeptideSearchResult("q21_p1", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"));
        FileBasedProteinToOLNMap proteinToOLNMapping = new FileBasedProteinToOLNMap();
        proteinToOLNMapping.addMapping("KPYK1_YEAST", "YAL038W");

        PeptideSequence sequence = new PeptideSequence("", "23M", 0, 0, 0, null);

        ResultsAnalyserOutputter outputter = new ResultsAnalyserOutputter(result, proteinToOLNMapping, genome, sequence);
        String expected = "KPYK1_YEAST\tYAL038W\tYAL038W\t25.95\t469\t474\t6\tchrI\t900\t920\t+2\t1\t900-922";

        assertEquals(expected, outputter.toString());
    }

}
