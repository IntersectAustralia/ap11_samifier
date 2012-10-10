package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 10/10/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GenomeParser
{
    public Genome parseGenomeFile(File genomeFile) throws GenomeFileParsingException;
}
