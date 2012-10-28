package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.domain.PeptideSearchResult;
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
    private Map<String,String> proteinToOLNMap;
    private File mapFile;

    public ReverseProteinRunner(String[] searchResultsPaths, File mapFile)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.mapFile = mapFile;
    }

    public void run() throws Exception
    {
        ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
        proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);

        PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);
        List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(searchResultsPaths);

    }
}
