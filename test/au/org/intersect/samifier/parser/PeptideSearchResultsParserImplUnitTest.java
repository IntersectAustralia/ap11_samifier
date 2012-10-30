package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PeptideSearchResultsParserImplUnitTest
{
    @Test
    public void testParsingMascotPeptideSearchResultsDatFormat()
    {
        File mascotFile = new File("test/resources/test_mascot_search_results.txt");

        File mapFile = new File("test/resources/test_accession.txt");
        try {
            ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
            ProteinToOLNMap proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);

            List<PeptideSearchResult> list = peptideSearchResultsParser.parseResults(mascotFile);
            assertEquals("Parser should find seven ", 7, list.size());

            assertTrue(list.contains(new PeptideSearchResult("q21_p1", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"))));
            assertTrue(list.contains(new PeptideSearchResult("q131_p1", "SVIDNAR", "KPYK1_YEAST", 62, 68 , new BigDecimal("40.45"))));
            assertTrue(list.contains(new PeptideSearchResult("q217_p1", "INFGIEK", "KPYK1_YEAST", 460, 466 , new BigDecimal("37.51"))));
            assertTrue(list.contains(new PeptideSearchResult("q376_p2", "TGIAIGLNK", "RL36B_YEAST", 5, 13 , new BigDecimal("35.86"))));
            assertTrue(list.contains(new PeptideSearchResult("q887_p1", "KRNEEEDAK", "RL31A_YEAST", 78, 86 , new BigDecimal("40.09"))));
            assertTrue(list.contains(new PeptideSearchResult("q1009_p2", "EYTINLHKR", "RL31A_YEAST", 11, 19 , new BigDecimal("52.75"))));
            assertTrue(list.contains(new PeptideSearchResult("q2365_p1", "NEEEDAKNPLFSYVEPVLVASAK", "RL31A_YEAST", 80, 102 , new BigDecimal("20.69"))));
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testParsingMascotPeptideSearchResultsMzidentMLFormat()
    {
        File mascotFile = new File("test/resources/test_mascot_search_results.mzid");

        File mapFile = new File("test/resources/test_accession.txt");
        try {
            ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
            ProteinToOLNMap proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);

            List<PeptideSearchResult> list = peptideSearchResultsParser.parseResults(mascotFile);
            assertEquals("Parser should find seven ", 7, list.size());

            assertTrue(list.contains(new PeptideSearchResult("PE_11_1_KPYK1_YEAST_0_469_474", "EFGILK", "KPYK1_YEAST", 469, 474 , new BigDecimal("25.95"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_11_2_KPYK1_YEAST_0_62_68", "SVIDNAR", "KPYK1_YEAST", 62, 68 , new BigDecimal("40.45"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_11_3_KPYK1_YEAST_0_460_466", "INFGIEK", "KPYK1_YEAST", 460, 466 , new BigDecimal("37.51"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_11_4_RL36B_YEAST_0_5_13", "TGIAIGLNK", "RL36B_YEAST", 5, 13 , new BigDecimal("35.86"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_14_1_RL31A_YEAST_0_78_86", "KRNEEEDAK", "RL31A_YEAST", 78, 86 , new BigDecimal("40.09"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_14_3_RL31A_YEAST_0_11_19", "EYTINLHKR", "RL31A_YEAST", 11, 19 , new BigDecimal("52.75"))));
            assertTrue(list.contains(new PeptideSearchResult("PE_14_11_RL31A_YEAST_0_80_102", "NEEEDAKNPLFSYVEPVLVASAK", "RL31A_YEAST", 80, 102 , new BigDecimal("20.69"))));
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.getCause().printStackTrace();
        }
    }
}
