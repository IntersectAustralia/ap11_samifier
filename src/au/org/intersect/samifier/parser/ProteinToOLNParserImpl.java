package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.FileBasedProteinToOLNMap;
import au.org.intersect.samifier.domain.ProteinToOLNMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProteinToOLNParserImpl implements ProteinToOLNParser {
    @Override
    public ProteinToOLNMap parseMappingFile(File mappingFile)
            throws ProteinToOLNMappingFileParsingException {
        try {
            return doFileParsing(mappingFile);
        } catch (IOException e) {
            throw new ProteinToOLNMappingFileParsingException(e.getMessage());
        }

    }

    private ProteinToOLNMap doFileParsing(File mappingFile) throws IOException,
            ProteinToOLNMappingFileParsingException {
        FileBasedProteinToOLNMap proteinOLN = new FileBasedProteinToOLNMap();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(mappingFile));

            // Skip header line
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.matches("^#.*$")) {
                    continue;
                }
                // ordered_locus_name accession_id protein_name id
                // Tab delimited
                String[] parts = line.split("\\s+");
                if (parts.length < 3) {
                    throw new ProteinToOLNMappingFileParsingException(
                            "Line "
                                    + lineNumber
                                    + " not in expected format, should be: ordered_locus_name accession_id protein_name id");
                }
                proteinOLN.addMapping(parts[2], parts[0]);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return proteinOLN;
    }

}
