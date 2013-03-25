package au.org.intersect.samifier.parser.mzidentml;

import org.xml.sax.helpers.DefaultHandler;

public class PeptideHandler extends DefaultHandler {
    private static final String PEPTIDE_SEQUENCE = "peptideSequence";
    private MzidReader reader;
    private String peptideId;
    private String peptideSequence;

    public PeptideHandler(MzidReader mzidReader, String peptideId) {
        super();
        this.reader = mzidReader;
        this.peptideId = peptideId;
        this.peptideSequence = "";
    }

    public void endElement(String uri, String name, String qName) {
        if (PEPTIDE_SEQUENCE.equalsIgnoreCase(qName)) {
            reader.storePeptide(peptideId, peptideSequence);
            reader.removeHandler();
        }
    }

    public void characters(char[] ch, int start, int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = start; i < start + length; i++) {
            sb.append(ch[i]);
        }
        if (sb.toString().trim().length() > 0) {
            peptideSequence = sb.toString();
        }
    }
}
