package au.org.intersect.samifier.parser;

import java.io.IOException;
import java.util.List;

import au.org.intersect.samifier.domain.GeneInfo;
import au.org.intersect.samifier.domain.NucleotideSequence;

public interface FastaParser {
    int getChromosomeLength(String chromosome);
    List<NucleotideSequence> extractSequenceParts(GeneInfo gene) throws IOException, FastaParserException;
    String readCode(String chromosomeName) throws IOException, FastaParserException;
    List <String> scanForChromosomes() throws FastaParserException;
}
