package au.org.intersect.samifier.parser.mzidentml;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
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
    private Map<String, String[]> peptideReferenceMap;
    private Map<String, String[]> peptideEvidenceMap;
    private String fileName;

    private List<PeptideSearchResult> results;
    private static Logger LOG = Logger.getLogger(MzidReader.class);

    public MzidReader(File resultsFile) {
        dbSequenceMap = new HashMap<String, String>();
        peptideMap = new HashMap<String, String>();
        results = new ArrayList<PeptideSearchResult>();
        peptideReferenceMap = new HashMap<String, String[]>();
        peptideEvidenceMap = new HashMap<String, String[]>();
        fileName = resultsFile.getName();
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
            processEvidence();
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
        PeptideSearchResult searchResult = new PeptideSearchResult(fileName, id, peptideSequence, protein, Integer.parseInt(start), Integer.parseInt(end), score);
        results.add(searchResult);
    }

    private void build(String id, String start, String end, String protein) {
        String[] params = peptideReferenceMap.get(id);
        if (params == null) {
            LOG.warn("No peptide ref for id " + id);
            return;
        }
        PeptideSearchResult searchResult = new PeptideSearchResult(fileName, id, params[0], protein, Integer.parseInt(start), Integer.parseInt(end), new BigDecimal(params[1]));
        results.add(searchResult);
    }

    private void switchHandler(DefaultHandler handler) {
        xmlReader.setContentHandler(handler);
    }

    public void addReference(String reference, String confidenceScore, String sequence) {
        String[] params = {sequence, confidenceScore};
        peptideReferenceMap.put(reference, params);
    }

    public void addPeptideEvidence(String id, String start, String end, String protein) {
       String [] params = {start, end, protein};
       peptideEvidenceMap.put(id, params);

    }
    
    private void processEvidence(){
        //in mzid 1.1 we have evidence entity that we need to process to get peptides 
        if (peptideEvidenceMap.size() > 0) {
            for (String key : peptideEvidenceMap.keySet()) {
                String[] params = peptideEvidenceMap.get(key);
                build(key, params[0], params[1], params[2]);
            }
        }
    }
}
