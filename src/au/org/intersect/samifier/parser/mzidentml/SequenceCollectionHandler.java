package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SequenceCollectionHandler extends DefaultHandler {
    private static final String SEQUENCE_COLLECITON = "SequenceCollection";
    private static final String DB_SEQUENCE = "DBSequence";
    private static final String PEPTIDE = "Peptide";
    private static final String PEPTIDE_EVIDENCE = "PeptideEvidence";
    private static final String ATTR_ID = "id";
    private static final String ATTR_ACCESSION = "accession";
    private static final String ATTR_START = "start";
    private static final String ATTR_END = "end";
    private static final String ATTR_DB_SEQUENCE_REF = "dBSequence_ref";
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
        } else if (PEPTIDE_EVIDENCE.equals(qName)) {
            // <PeptideEvidence id="PE_6_2_A1AT_BOVIN_0_191_196" start="191" end="196" pre="K" post="V" peptide_ref="peptide_6_2" isDecoy="false" dBSequence_ref="DBSeq_2_A1AT_BOVIN" />
            String id = attrs.getValue(ATTR_ID);
            String start = attrs.getValue(ATTR_START);
            String end = attrs.getValue(ATTR_END);
            String protein = reader.getAccessionFromDbSequenceRef(attrs.getValue(ATTR_DB_SEQUENCE_REF));
            reader.addPeptideEvidence(id, start, end, protein);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (SEQUENCE_COLLECITON.equals(qName)) {
            reader.removeHandler();
        }
    }
}
