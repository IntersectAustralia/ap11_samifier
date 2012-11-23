package au.org.intersect.samifier.parser.mzidentml;

import java.io.File;

import org.xml.sax.SAXException;

public class PeptideSearchResultsParser
{
	public PeptideSearchResultsParser(File resultsFile) throws SAXException
	{
		MzidReader reader = new MzidReader(resultsFile);
		MzIdentMLHandler mzIdentMLHandler = new MzIdentMLHandler(reader);
		reader.pushHandler(mzIdentMLHandler);
		reader.run();
	}
}
