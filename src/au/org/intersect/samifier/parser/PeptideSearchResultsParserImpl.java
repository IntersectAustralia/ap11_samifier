package au.org.intersect.samifier.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import au.org.intersect.samifier.domain.GeneInfo;
import au.org.intersect.samifier.domain.Genome;
import au.org.intersect.samifier.domain.PeptideSearchResult;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.domain.ProteinToOLNMap;
import au.org.intersect.samifier.parser.mzidentml.MzIdentMLHandler;
import au.org.intersect.samifier.parser.mzidentml.MzidReader;

public class PeptideSearchResultsParserImpl implements
        PeptideSearchResultsParser {

    private static Logger LOG = Logger.getLogger(PeptideSearchResultsParserImpl.class);
    private static Pattern linePattern = Pattern.compile("^(q\\d+_p\\d+)=([^;]+);(.+)$");
    private static Pattern proteinPartPattern = Pattern.compile("^\"([^\"]+)\":\\d\\:(\\d+)\\:(\\d+)\\:\\d$");

    private ProteinToOLNMap proteinToOLNMapping;

    public PeptideSearchResultsParserImpl(ProteinToOLNMap proteinToOLNMapping) {
        this.proteinToOLNMapping = proteinToOLNMapping;
    }

    @Override
    public List<PeptideSearchResult> parseResults(String[] searchResultsPaths)
            throws MascotParsingException {
        List<PeptideSearchResult> peptideSearchResults = new ArrayList<PeptideSearchResult>();
        List<File> searchResultFiles = new ArrayList<File>();
        for (String searchResultsPath : searchResultsPaths) {
            File searchResultFile = new File(searchResultsPath);
            if (!searchResultFile.exists()) {
                System.err.println(searchResultFile + " does not exist");
                System.exit(1);
            }
            searchResultFiles.add(searchResultFile);
        }

        for (File searchResultFile : searchResultFiles) {
            LOG.debug("Processing: " + searchResultFile.getAbsolutePath());
            peptideSearchResults.addAll(parseResults(searchResultFile));
        }
        //remove all peptides with "X"
        Iterator<PeptideSearchResult> setIterator = peptideSearchResults.iterator();
        while (setIterator.hasNext()) {
            PeptideSearchResult currentElement = setIterator.next();
            if (currentElement.getPeptideSequence().contains("X")) {
                setIterator.remove();
            }
        }
        return peptideSearchResults;
    }

    @Override
    public List<PeptideSearchResult> parseResults(File searchResultFile)
            throws MascotParsingException {
        try {
            BufferedReader headerReader = new BufferedReader(new FileReader(
                    searchResultFile));
            String firstLine = headerReader.readLine();
            headerReader.close();
            // Detect mzidentML format or text format
            if (firstLine.startsWith("<?xml ")) {
                return parseMascotPeptideSearchResultsMzidentMLFormat(searchResultFile);
            } else {
                return parseMascotPeptideSearchResultsDATFormat(searchResultFile);
            }
        } catch (IOException e) {
            throw new MascotParsingException(e);
        }
    }

    public List<PeptideSearchResult> parseMascotPeptideSearchResultsDATFormat(
            File resultsFile) throws MascotParsingException {
        BufferedReader reader = null;
        boolean peptidesSectionStarted = false;
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        try {
            reader = new BufferedReader(new FileReader(resultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (peptidesSectionStarted) {
                    if (line.startsWith("--")) {
                        break;
                    }
                    results.addAll(getProteinsFromQueryLine(resultsFile.getName(),  line));
                } else if (line.startsWith("Content-Type: application/x-Mascot; name=\"peptides\"") ||
                           line.startsWith("Content-Type: application/x-Mascot; name=peptides")) {
                    peptidesSectionStarted = true;
                }
            }
            if (!peptidesSectionStarted) {
                throw new MascotParsingException(new Exception("Peptide section not found in file"));
            }
        } catch (Exception e) {
            throw new MascotParsingException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new MascotParsingException(e);
            }
        }
        return results;
    }

    public List<PeptideSearchResult> parseMascotPeptideSearchResultsMzidentMLFormat(
            File resultsFile) throws MascotParsingException {
        MzidReader reader = new MzidReader(resultsFile);
        MzIdentMLHandler mzIdentMLHandler = new MzIdentMLHandler(reader);
        reader.pushHandler(mzIdentMLHandler);
        return reader.run();
    }

    public List<PeptideSearchResult> sortResultsByChromosome(
            List<PeptideSearchResult> searchResult,
            ProteinToOLNMap proteinToOLNMap, Genome genome) {
        List<PeptideSearchResult> result = new ArrayList<PeptideSearchResult>();
        HashMap<String, String> proteinIDToChromosome = new HashMap<String, String>();
        for (PeptideSearchResult res : searchResult) {
            String proteinID = proteinToOLNMap.getOLN(res.getProteinName());
            GeneInfo genInfo = genome.getGene(proteinID);
            if (genInfo == null) {
                LOG.warn("No gene location for " + proteinID);
                continue;
            }
            String chromosome = genInfo.getChromosome();
            proteinIDToChromosome.put(res.getProteinName(), chromosome);
        }
        Set<String> chromosomes = new HashSet<String>(
                proteinIDToChromosome.values());
        for (String chromosome : chromosomes) {
            for (PeptideSearchResult res : searchResult) {
                if (proteinIDToChromosome.containsKey(res.getProteinName())) {
                    if (proteinIDToChromosome.get(res.getProteinName()).equals(chromosome)) {
                        result.add(res);
                    }
                }
            }
        }
        return result;

    }

    private List<PeptideSearchResult> getProteinsFromQueryLine(String fileName, String line)
            throws IOException {
        List<PeptideSearchResult> results = new ArrayList<PeptideSearchResult>();
        // Expected format:
        // q21_p1=0,705.406113,-0.000065,4,EFGILK,18,00000000,25.95,0000000001000002010,0,0;"KPYK1_YEAST":0:469:474:1,"RL31B_YEAST":0:78:86:1
        Pattern linePattern = Pattern.compile("^(q\\d+_p\\d+)=([^;]+);(.+)$");
        Pattern proteinPartPattern = Pattern
                .compile("^\"([^\"]+)\":\\d\\:(\\d+)\\:(\\d+)\\:\\d$");
        Matcher lineMatcher = linePattern.matcher(line);

        if (lineMatcher.matches()) {
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
            for (String proteinPart : proteins) {
                // Expected format:
                // "KPYK1_YEAST":0:469:474:1
                Matcher proteinPartMatcher = proteinPartPattern
                        .matcher(proteinPart);
                if (proteinPartMatcher.matches()) {
                    String protein = proteinPartMatcher.group(1);
                    if (!proteinToOLNMapping.containsProtein(protein)) {
                        LOG.info("Protein ID not found in accession file");
                        LOG.info("ERR_ACC: " + protein);
                        continue;
                    }
                    int start = Integer.parseInt(proteinPartMatcher.group(2));
                    int stop = Integer.parseInt(proteinPartMatcher.group(3));
                    results.add(new PeptideSearchResult(fileName, id, peptideSequence,
                            protein, start, stop, confidenceScore));
                }
            }
        }
        return results;
    }

}
