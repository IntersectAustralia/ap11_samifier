package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.MascotParsingException;
import au.org.intersect.samifier.PeptideSearchResult;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 3/10/12
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PeptideSearchResultsParser
{
    List<PeptideSearchResult> parseResults(File searchResultFile) throws MascotParsingException;
}
