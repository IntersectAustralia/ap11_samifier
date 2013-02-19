package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SequenceCollectionHandler extends DefaultHandler {
    private static final String SEQUENCE_COLLECITON = "SequenceCollection";
    private static final String DB_SEQUENCE = "DBSequence";
    private static final String PEPTIDE = "Peptide";
    private static final String ATTR_ID = "id";
    private static final String ATTR_ACCESSION = "accession";
    private MzidReader reader;

    public SequenceCollectionHandler(MzidReader mzidReader) {
        super();
        this.reader = mzidReader;
    }

    public void startElement(String uri, String name, String qName,
            Attributes attrs) {
        if (DB_SEQUENCE.equals(qName)) {
            reader.storeDbSequence(attrs.getValue(ATTR_ID),
                    attrs.getValue(ATTR_ACCESSION));
        } else if (PEPTIDE.equals(qName)) {
            PeptideHandler peptideHandler = new PeptideHandler(reader,
                    attrs.getValue(ATTR_ID));
            reader.pushHandler(peptideHandler);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (SEQUENCE_COLLECITON.equals(qName)) {
            reader.removeHandler();
        }
    }
}
