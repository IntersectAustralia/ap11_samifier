package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MzIdentMLHandler extends DefaultHandler
{
	private static final String MZIDENTML = "mzIdentML";
	private static final String SEQUENCE_COLLECITON = "SequenceCollection";
	private static final String DATA_COLLECTION = "DataCollection";
	private MzidReader reader;
	
	public MzIdentMLHandler(MzidReader mzidReader)
	{
		super();
		reader = mzidReader;
	}
	
	public void startElement(String uri, String name, String qName, Attributes attrs)
	{
		if (MZIDENTML.equals(qName))
		{
			System.out.println("Qualifier name:" + qName);
			return;
		}
		else if (SEQUENCE_COLLECITON.equals(qName))
		{
			System.out.println("New handler:" + qName);
			SequenceCollectionHandler sequenceHandler = new SequenceCollectionHandler(reader);
			reader.pushHandler(sequenceHandler);
		}
		else if (DATA_COLLECTION.equals(qName))
		{
			System.out.println("New handler:" + qName);
			DataCollectionHandler dataHandler = new DataCollectionHandler(reader);
			reader.pushHandler(dataHandler);
		}
	}
	
	public void endElement(String uri, String name, String qName)
	{
		if (MZIDENTML.equals(qName))
		{
			// Build all
		}
	}
	
	public void characters(char ch[], int start, int length)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = start; i < start + length; i++)
		{
			sb.append(ch[i]);
		}
		//if (sb.toString().trim().length() > 0)
		//	System.out.println("MzIdenMLHandler print: " + sb.toString());
	}
	
	public void startDocument()
	{
		System.out.println("Start document");
	}
	
	public void endDocument()
	{
		System.out.println("End document!");
	}
}
