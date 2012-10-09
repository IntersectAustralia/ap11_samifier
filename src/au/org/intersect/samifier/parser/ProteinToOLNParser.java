package au.org.intersect.samifier.parser;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 10/10/12
 * Time: 8:30 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ProteinToOLNParser
{
    public Map<String,String> parseMappingFile(File mappingFile) throws ProteinToOLNMappingFileParsingException;
}
