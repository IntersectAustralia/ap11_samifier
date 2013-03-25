package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MzIdentMLHandler extends DefaultHandler {
    private static final String MZIDENTML = "mzIdentML";
    private static final String SEQUENCE_COLLECITON = "SequenceCollection";
    private static final String DATA_COLLECTION = "DataCollection";
    private MzidReader reader;

    public MzIdentMLHandler(MzidReader mzidReader) {
        super();
        reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        if (MZIDENTML.equals(qName)) {
            return;
        } else if (SEQUENCE_COLLECITON.equals(qName)) {
            SequenceCollectionHandler sequenceHandler = new SequenceCollectionHandler(reader);
            reader.pushHandler(sequenceHandler);
        } else if (DATA_COLLECTION.equals(qName)) {
            DataCollectionHandler dataHandler = new DataCollectionHandler(reader);
            reader.pushHandler(dataHandler);
        }
    }

    public void startDocument() {
        System.out.println("Start document");
    }

    public void endDocument() {
        System.out.println("End document!");
    }
}
