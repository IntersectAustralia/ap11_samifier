package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeptideSearchResultsParserImpl implements PeptideSearchResultsParser
{

    private static Logger LOG = Logger.getLogger(PeptideSearchResultsParserImpl.class);

    private ProteinToOLNMap proteinToOLNMapping;

    public PeptideSearchResultsParserImpl(ProteinToOLNMap proteinToOLNMapping)
    {
        this.proteinToOLNMapping = proteinToOLNMapping;
    }

    @Override
    public List<PeptideSearchResult> parseResults(String[] searchResultsPaths) throws MascotParsingException
    {
        List<PeptideSearchResult> peptideSearchResults = new ArrayList<PeptideSearchResult>();
        List<File> searchResultFiles = new ArrayList<File>();
        for (String searchResultsPath : searchResultsPaths)
        {
            File searchResultFile = new File(searchResultsPath);
            if (!searchResultFile.exists())
            {
                System.err.println(searchResultFile + " does not exist");
                System.exit(1);
            }
            searchResultFiles.add(searchResultFile);
        }

        for (File searchResultFile : searchResultFiles)
        {
            LOG.debug("Processing: " + searchResultFile.getAbsolutePath());
            peptideSearchResults.addAll(parseResults(searchResultFile));
        }
        return peptideSearchResults;
    }


    @Override
    public List<PeptideSearchResult> parseResults(File searchResultFile) throws MascotParsingException
    {
        try
        {
            BufferedReader headerReader = new BufferedReader(new FileReader(searchResultFile));
            String firstLine = headerReader.readLine();
            headerReader.close();
            // Detect mzidentML format or text format
            if (firstLine.startsWith("<?xml "))
            {
                return parseMascotPeptideSearchResultsMzidentMLFormat(searchResultFile);
            }
            else
            {
                return parseMascotPeptideSearchResultsDATFormat(searchResultFile);
            }
        }
        catch (IOException e)
        {
            throw new MascotParsingException(e);
        }
    }

    public List<PeptideSearchResult> parseMascotPeptideSearchResultsDATFormat(File resultsFile)
            throws MascotParsingException
    {
        BufferedReader reader = null;
        boolean peptidesSectionStarted = false;
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        try {
            reader = new BufferedReader(new FileReader(resultsFile));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (peptidesSectionStarted)
                {
                    if (line.startsWith("--"))
                    {
                        break;
                    }
                    results.addAll(getProteinsFromQueryLine(line));
                }
                else if (line.startsWith("Content-Type: application/x-Mascot; name=\"peptides\""))
                {
                    peptidesSectionStarted = true;
                }
            }
        }
        catch (Exception e)
        {
            throw new MascotParsingException(e);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                throw new MascotParsingException(e);
            }
        }
        return results;
    }

    public List<PeptideSearchResult> parseMascotPeptideSearchResultsMzidentMLFormat(File resultsFile)
            throws MascotParsingException
    {
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();

        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();

            xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if (prefix == null) throw new NullPointerException("Null prefix");
                    else if ("mzidentml".equals(prefix)) return "http://psidev.info/psi/pi/mzIdentML/1.0";
                    return XMLConstants.NULL_NS_URI;
                }
                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException();
                }

                public Iterator getPrefixes(String uri) {
                    throw new UnsupportedOperationException();
                }
            });


            String xPathStr = "//mzidentml:Peptide";
            InputSource iS = new InputSource(new FileReader(resultsFile));
            Node root = (Node) xPath.evaluate("/", iS, XPathConstants.NODE);

            QName nodesetType = XPathConstants.NODESET;
            QName nodeType = XPathConstants.NODE;

            NodeList peptideList = (NodeList)xPath.evaluate(xPathStr, root, nodesetType);
            for (int peptideIndex = 0 ; peptideIndex < peptideList.getLength(); peptideIndex++)
            {
                Node peptideNode = peptideList.item(peptideIndex);
                String peptideId = peptideNode.getAttributes().getNamedItem("id").getNodeValue();
                String peptideSequenceXpath = "./mzidentml:peptideSequence";
                Node peptideSequenceNode = (Node)xPath.evaluate(peptideSequenceXpath, peptideNode, nodeType);
                String peptideSequence = peptideSequenceNode.getTextContent();

                String peptideInfoXpath = "//mzidentml:SpectrumIdentificationItem[@Peptide_ref='" + peptideId + "']";
                Node peptideInfo = (Node)xPath.evaluate(peptideInfoXpath, root, nodeType);

                String peptideEvidenceXpath = "./mzidentml:PeptideEvidence";
                String confidenceScoreXpath = "./mzidentml:cvParam[@name='mascot:score']";

                NodeList peptideEvidenceList = (NodeList)xPath.evaluate(peptideEvidenceXpath, peptideInfo, nodesetType);
                Node confidenceScoreNode = (Node)xPath.evaluate(confidenceScoreXpath, peptideInfo, nodeType);
                String confidenceScoreStr = confidenceScoreNode.getAttributes().getNamedItem("value").getNodeValue();
                BigDecimal confidenceScore = new BigDecimal(confidenceScoreStr);

                for (int peptideEvidenceIndex = 0; peptideEvidenceIndex < peptideEvidenceList.getLength(); peptideEvidenceIndex++)
                {
                    Node peptideEvidence = peptideEvidenceList.item(peptideEvidenceIndex);
                    String id = peptideEvidence.getAttributes().getNamedItem("id").getNodeValue();
                    String start = peptideEvidence.getAttributes().getNamedItem("start").getNodeValue();
                    String stop = peptideEvidence.getAttributes().getNamedItem("end").getNodeValue();
                    String dbSequenceRef = peptideEvidence.getAttributes().getNamedItem("DBSequence_Ref").getNodeValue();

                    String dbSequenceXpath = "//mzidentml:DBSequence[@id='" + dbSequenceRef + "']";
                    Node dbSequence = (Node)xPath.evaluate(dbSequenceXpath, root, nodeType);
                    String protein = dbSequence.getAttributes().getNamedItem("accession").getNodeValue();
                    if (!proteinToOLNMapping.containsProtein(protein))
                    {
                        LOG.info(protein + " not found in given accession mapping file");
                        continue;
                    }
                    results.add(new PeptideSearchResult(id, peptideSequence, protein, Integer.parseInt(start), Integer.parseInt(stop), confidenceScore));
                }
            }
        }
        catch (Exception e)
        {
            throw new MascotParsingException(e);
        }


        return results;
    }


    private List<PeptideSearchResult> getProteinsFromQueryLine(String line)
            throws IOException
    {
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        // Expected format:
        // q21_p1=0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0;"KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1
        Pattern linePattern = Pattern.compile("^(q\\d+_p\\d+)=([^;]+);(.+)$");
        Pattern proteinPartPattern = Pattern.compile("^\"([^\"]+)\":\\d\\:(\\d+)\\:(\\d+)\\:\\d$");
        Matcher lineMatcher = linePattern.matcher(line);

        if (lineMatcher.matches())
        {
            String id = lineMatcher.group(1);
            String peptidePart = lineMatcher.group(2);
            String proteinsPart = lineMatcher.group(3);

            // Expected format:
            // 0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0
            String[] peptideParts = peptidePart.split(",");
            String peptideSequence = peptideParts[4];
            BigDecimal confidenceScore = new BigDecimal(peptideParts[7]);

            // Expected format:
            // "KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1, ...
            String[] proteins = proteinsPart.split(",");
            for (String proteinPart : proteins)
            {
                // Expected format:
                // "KPYK1_YEAST":0:469:474:1
                Matcher proteinPartMatcher = proteinPartPattern.matcher(proteinPart);
                if (proteinPartMatcher.matches())
                {
                    String protein = proteinPartMatcher.group(1);
                    if (!proteinToOLNMapping.containsProtein(protein))
                    {
                        LOG.info("Protein ID not found in accession file");
                        LOG.info("ERR_ACC: " + protein);
                        continue;
                    }
                    int start = Integer.parseInt(proteinPartMatcher.group(2));
                    int stop  = Integer.parseInt(proteinPartMatcher.group(3));
                    results.add(new PeptideSearchResult(id, peptideSequence, protein, start, stop, confidenceScore));
                }
            }
        }
        return results;
    }


}
