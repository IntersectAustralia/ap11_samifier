package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class AnalysisDataHandler extends DefaultHandler {
    private static final String ANALYSIS_DATA = "AnalysisData";
    private static final String SPECTRUM_ID_LIST = "SpectrumIdentificationList";
    private MzidReader reader;

    public AnalysisDataHandler(MzidReader mzidReader) {
        super();
        this.reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        if (SPECTRUM_ID_LIST.equals(qName)) {
            SpectrumIdentificationListHandler spectrumListHandler = new SpectrumIdentificationListHandler(
                    reader);
            reader.pushHandler(spectrumListHandler);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (ANALYSIS_DATA.equals(qName)) {
            reader.removeHandler();
        }
    }
}
