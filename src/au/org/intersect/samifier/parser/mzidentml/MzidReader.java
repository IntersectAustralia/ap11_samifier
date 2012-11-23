package au.org.intersect.samifier.parser.mzidentml;

import java.io.File;
import java.io.FileReader;
import java.util.Stack;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MzidReader 
{	
	private XMLReader xmlReader;
	private InputSource inputSource;
	private Stack<DefaultHandler> handlerStack;
	
	public MzidReader(File resultsFile)
	{
		try 
		{
			inputSource = new InputSource(new FileReader(resultsFile));
			xmlReader = XMLReaderFactory.createXMLReader();
			handlerStack = new Stack<DefaultHandler>();
		} 
		catch (Exception e) 
		{
			System.out.println("Exception thrown");
			e.printStackTrace();
		}
	}
	
	public Object run()
	{
		try
		{
			xmlReader.parse(inputSource);
			return buildResponse();
		}
		catch (Exception e)
		{
			System.out.println("Run exception thrown");
			e.printStackTrace();
			return null;
		}
	}

	public void pushHandler(DefaultHandler handler)
	{
		handlerStack.push(handler);
		switchHandler(handlerStack.peek());
	}
	
	public void removeHandler()
	{
		handlerStack.pop();
		switchHandler(handlerStack.peek());
	}
	
	private void switchHandler(DefaultHandler handler)
	{
		xmlReader.setContentHandler(handler);
	}
	
	private Object buildResponse() {
		// TODO Auto-generated method stub
		return null;
	}

}
