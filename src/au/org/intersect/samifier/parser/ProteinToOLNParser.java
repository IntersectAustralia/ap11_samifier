package au.org.intersect.samifier.parser;

import java.io.File;
import java.util.Map;

public interface ProteinToOLNParser
{
    public Map<String,String> parseMappingFile(File mappingFile) throws ProteinToOLNMappingFileParsingException;
}
