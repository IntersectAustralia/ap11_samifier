package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PeptideHandler extends DefaultHandler 
{
	private static final String PEPTIDE_SEQUENCE = "peptideSequence";
	private MzidReader reader;
	
	public PeptideHandler(MzidReader mzidReader)
	{
		super();
		this.reader = mzidReader;
	}
	
	public void startElement(String uri, String name, String qName, Attributes attrs)
	{
		if (PEPTIDE_SEQUENCE.equals(qName))
		{
			System.out.println("<The peptide sequence value>");
		}	 
	}
	
	public void endElement(String uri, String name, String qName)
	{
		if (PEPTIDE_SEQUENCE.equals(qName))
		{
			reader.removeHandler();
		}	 
	}	
}
