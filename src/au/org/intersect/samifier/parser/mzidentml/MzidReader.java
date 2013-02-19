package au.org.intersect.samifier.parser.mzidentml;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import au.org.intersect.samifier.domain.PeptideSearchResult;

public class MzidReader {
    private XMLReader xmlReader;
    private InputSource inputSource;
    private Stack<DefaultHandler> handlerStack;

    private Map<String, String> dbSequenceMap;
    private Map<String, String> peptideMap;

    private List<PeptideSearchResult> results;

    public MzidReader(File resultsFile) {
        dbSequenceMap = new HashMap<String, String>();
        peptideMap = new HashMap<String, String>();
        results = new ArrayList<PeptideSearchResult>();

        try {
            inputSource = new InputSource(new FileReader(resultsFile));
            xmlReader = XMLReaderFactory.createXMLReader();
            handlerStack = new Stack<DefaultHandler>();
        } catch (Exception e) {
            System.out.println("Exception thrown");
            e.printStackTrace();
        }
    }

    public List<PeptideSearchResult> run() {
        try {
            xmlReader.parse(inputSource);
            return results;
        } catch (Exception e) {
            System.out.println("Run exception thrown");
            e.printStackTrace();
            return null;
        }
    }

    public void pushHandler(DefaultHandler handler) {
        handlerStack.push(handler);
        switchHandler(handlerStack.peek());
    }

    public void removeHandler() {
        handlerStack.pop();
        switchHandler(handlerStack.peek());
    }

    public void storeDbSequence(String id, String accession) {
        dbSequenceMap.put(id, accession);
    }

    public void storePeptide(String peptideId, String peptideSequence) {
        peptideMap.put(peptideId, peptideSequence);
    }

    public String getSequenceFromPeptideRef(String peptideId) {
        for (Map.Entry<String, String> peptide : peptideMap.entrySet()) {
            if (peptide.getKey().equals(peptideId)) {
                return peptide.getValue();
            }
        }
        return "";
    }

    public String getAccessionFromDbSequenceRef(String dbSequenceId) {
        for (Map.Entry<String, String> dbSequence : dbSequenceMap.entrySet()) {
            if (dbSequence.getKey().equals(dbSequenceId)) {
                return dbSequence.getValue();
            }
        }
        return "";
    }

    public void build(String id, String peptideSequence, String protein,
            String start, String end, String confidenceScore) {
        BigDecimal score = new BigDecimal(confidenceScore);
        PeptideSearchResult searchResult = new PeptideSearchResult(id,
                peptideSequence, protein, Integer.parseInt(start),
                Integer.parseInt(end), score);
        results.add(searchResult);
    }

    public void seeResults() {
        for (PeptideSearchResult result : results) {
            System.out.println(result.toString());
        }
        System.out.println("Size of results: " + results.size());
    }

    private void switchHandler(DefaultHandler handler) {
        xmlReader.setContentHandler(handler);
    }
}
