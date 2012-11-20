package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;

import java.io.File;

public interface GenomeParser
{

    public static final int CHROMOSOME_PART = 0;
    public static final int TYPE_PART = 2;
    public static final int ATTRIBUTES_PART = 8;
    public static final int START_PART = 3;
    public static final int STOP_PART = 4;
    public static final int STRAND_PART = 6;

    public Genome parseGenomeFile(File genomeFile) throws GenomeFileParsingException;
}
