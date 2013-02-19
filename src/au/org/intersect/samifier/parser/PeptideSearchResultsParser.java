package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;
import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinToOLNMap;

import java.io.File;
import java.util.List;

public interface PeptideSearchResultsParser {
    List<PeptideSearchResult> parseResults(File searchResultFile)
            throws MascotParsingException;

    List<PeptideSearchResult> parseResults(String[] searchResultFiles)
            throws MascotParsingException;

    List<PeptideSearchResult> sortResultsByChromosome(
            List<PeptideSearchResult> searchResult,
            ProteinToOLNMap proteinToOLNMap, Genome genome);

}
