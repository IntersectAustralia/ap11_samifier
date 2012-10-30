package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.ProteinToOLNMap;

import java.io.File;
import java.util.Map;

public interface ProteinToOLNParser
{
    public ProteinToOLNMap parseMappingFile(File mappingFile) throws ProteinToOLNMappingFileParsingException;
}
