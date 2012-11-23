package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SequenceCollectionHandler extends DefaultHandler
{
	private static final String SEQUENCE_COLLECITON = "SequenceCollection";
	private static final String DB_SEQUENCE = "DBSequence";
	private static final String PEPTIDE = "Peptide";
	private MzidReader reader;

	public SequenceCollectionHandler(MzidReader mzidReader)
	{
		super();
		this.reader = mzidReader;
	}
	
	public void startElement(String uri, String name, String qName, Attributes attrs)
	{
		if (DB_SEQUENCE.equals(qName))
		{
			System.out.println("DBSequence attrs: N/A");
		}
		else if (PEPTIDE.equals(qName))
		{
			System.out.println("New handler for peptides:" + qName);
			PeptideHandler peptideHandler = new PeptideHandler(reader);
			reader.pushHandler(peptideHandler);
		}
	}

	public void endElement(String uri, String name, String qName)
	{
		if (SEQUENCE_COLLECITON.equals(qName))
		{
			reader.removeHandler();
		}
	}
}
