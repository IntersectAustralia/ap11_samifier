package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.ProteinToOLNMap;

import java.io.File;

public interface ProteinToOLNParser {
    ProteinToOLNMap parseMappingFile(File mappingFile)
            throws ProteinToOLNMappingFileParsingException;
}
