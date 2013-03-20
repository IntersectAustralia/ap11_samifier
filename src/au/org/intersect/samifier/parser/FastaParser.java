package au.org.intersect.samifier.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import au.org.intersect.samifier.domain.GeneInfo;
import au.org.intersect.samifier.domain.NucleotideSequence;

public interface FastaParser {
    int getChromosomeLength(String chromosome);
    List<NucleotideSequence> extractSequenceParts(File chromosomeFile, GeneInfo gene) throws IOException, FastaParserException;
}
