package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SpectrumIdentificationResultHandler extends DefaultHandler {
    private static final String SPECTRUM_ID_ITEM = "SpectrumIdentificationItem";
    private static final String SPECTRUM_ID_RESULT = "SpectrumIdentificationResult";
    private static final String ATTR_PEPTIDE_REF = "Peptide_ref";
    private static final String ATTR_PEPTIDE_REF_LC = "peptide_ref";
    private MzidReader reader;

    public SpectrumIdentificationResultHandler(MzidReader mzidReader) {
        super();
        this.reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        String peptideSeq = reader.getSequenceFromPeptideRef(attrs
                .getValue(ATTR_PEPTIDE_REF));
        if (peptideSeq == null || peptideSeq.length() == 0) {
            peptideSeq = reader.getSequenceFromPeptideRef(attrs
                    .getValue(ATTR_PEPTIDE_REF_LC));
        }
        if (SPECTRUM_ID_ITEM.equals(qName) && !peptideSeq.isEmpty()) {
            SpectrumIdentificationItemHandler spectrumIdItemHandler = new SpectrumIdentificationItemHandler(
                    reader, peptideSeq);
            reader.pushHandler(spectrumIdItemHandler);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (SPECTRUM_ID_RESULT.equals(qName)) {
            reader.removeHandler();
        }
    }
}
