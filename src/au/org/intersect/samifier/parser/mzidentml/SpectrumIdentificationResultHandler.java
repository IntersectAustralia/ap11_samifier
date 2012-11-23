package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SpectrumIdentificationResultHandler extends DefaultHandler
{
	private static final String SPECTRUM_ID_ITEM = "SpectrumIdentificationItem";
	private static final String SPECTRUM_ID_RESULT = "SpectrumIdentificationResult";
	private MzidReader reader;
	
	public SpectrumIdentificationResultHandler(MzidReader mzidReader)
	{
		super();
		this.reader = mzidReader;
	}
	
	public void startElement(String uri, String name, String qName, Attributes attrs)
	{
		if (SPECTRUM_ID_ITEM.equals(qName))
		{
			System.out.println("<Spectrum Item PeptideEvidence and cvParam>");
			// create item handler
		}	 
	}
	
	public void endElement(String uri, String name, String qName)
	{
		if (SPECTRUM_ID_RESULT.equals(qName))
		{
			reader.removeHandler();
		}	 
	}
}
