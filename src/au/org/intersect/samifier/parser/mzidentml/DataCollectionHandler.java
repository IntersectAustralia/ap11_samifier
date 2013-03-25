package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DataCollectionHandler extends DefaultHandler {
    private static final String ANALYSIS_DATA = "AnalysisData";
    private static final String DATA_COLLECTION = "DataCollection";
    private MzidReader reader;

    public DataCollectionHandler(MzidReader mzidReader) {
        super();
        this.reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        if (ANALYSIS_DATA.equals(qName)) {
            AnalysisDataHandler analysisHandler = new AnalysisDataHandler(reader);
            reader.pushHandler(analysisHandler);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (DATA_COLLECTION.equals(qName)) {
            reader.removeHandler();
        }
    }
}
