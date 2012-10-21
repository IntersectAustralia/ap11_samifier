package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;

import java.io.File;

public interface GenomeParser
{
    public Genome parseGenomeFile(File genomeFile) throws GenomeFileParsingException;
}
