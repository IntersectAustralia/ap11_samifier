package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;

import java.io.File;

public interface GenomeParser {

    int CHROMOSOME_PART = 0;
    int TYPE_PART = 2;
    int ATTRIBUTES_PART = 8;
    int START_PART = 3;
    int STOP_PART = 4;
    int STRAND_PART = 6;

    Genome parseGenomeFile(File genomeFile) throws GenomeFileParsingException;
}
