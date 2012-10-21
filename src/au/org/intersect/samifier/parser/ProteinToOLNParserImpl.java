package au.org.intersect.samifier.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProteinToOLNParserImpl implements ProteinToOLNParser
{
    @Override
    public Map<String, String> parseMappingFile(File mappingFile) throws ProteinToOLNMappingFileParsingException
    {
        try
        {
            return doFileParsing(mappingFile);
        }
        catch (IOException e)
        {
            throw new ProteinToOLNMappingFileParsingException(e.getMessage());
        }

    }

    private Map<String, String> doFileParsing(File mappingFile) throws IOException, ProteinToOLNMappingFileParsingException {
        Map<String,String> proteinOLN = new HashMap<String,String>();

        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(mappingFile));

            // Skip header line
            String line = reader.readLine();
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (line.matches("^#.*$"))
                {
                    continue;
                }
                // ordered_locus_name accession_id protein_name id
                // Tab delimited
                String[] parts = line.split("\\s+");
                if (parts.length < 3)
                {
                    throw new ProteinToOLNMappingFileParsingException("Line "+lineNumber+" not in expected format, should be: ordered_locus_name accession_id protein_name id");
                }
                proteinOLN.put(parts[2], parts[0]);
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return proteinOLN;
    }


}
