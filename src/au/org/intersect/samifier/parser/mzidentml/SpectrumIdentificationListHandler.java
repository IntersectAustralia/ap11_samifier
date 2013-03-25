package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SpectrumIdentificationListHandler extends DefaultHandler {
    private static final String SPECTRUM_ID_LIST = "SpectrumIdentificationList";
    private static final String SPECTRUM_ID_RESULT = "SpectrumIdentificationResult";
    private MzidReader reader;

    public SpectrumIdentificationListHandler(MzidReader mzidReader) {
        super();
        this.reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        if (SPECTRUM_ID_RESULT.equals(qName)) {
            SpectrumIdentificationResultHandler spectrumResultHandler = new SpectrumIdentificationResultHandler(
                    reader);
            reader.pushHandler(spectrumResultHandler);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (SPECTRUM_ID_LIST.equals(qName)) {
            reader.removeHandler();
        }
    }
}
