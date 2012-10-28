package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.domain.EqualProteinOLNMap;
import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ReverseProteinRunner
{
    private String[] searchResultsPaths;
    private ProteinToOLNMap proteinToOLNMap;

    public ReverseProteinRunner(String[] searchResultsPaths)
    {
        this.searchResultsPaths = searchResultsPaths;
    }

    public void run() throws Exception
    {
        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        proteinToOLNMap = new EqualProteinOLNMap();

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);

    }
}
