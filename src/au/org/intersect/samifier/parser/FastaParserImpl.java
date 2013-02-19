package au.org.intersect.samifier.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.intersect.samifier.domain.GeneInfo;
import au.org.intersect.samifier.domain.GeneSequence;
import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.NucleotideSequence;

public class FastaParserImpl implements FastaParser {
    // private HashMap<String, String> codes = new HashMap<String, String>();
    private String previousFile;
    private String previousCode;

    public String readCode(File chromosomeFile) throws IOException {
        if (!chromosomeFile.exists()) {
            throw new FileNotFoundException(chromosomeFile.getAbsolutePath()
                    + " not found");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(chromosomeFile));
            // Skip header of FASTA file
            String line = reader.readLine();
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                // line = line.replace("\r", "").replace("\n", "");
                buffer.append(line);
            }
            return buffer.toString().replace("\r", "").replace("\n", "");
            // StringUtils.replace(buffer.toString();
        } finally {
            reader.close();
        }

    }

    @Override
    public List<NucleotideSequence> extractSequenceParts(File chromosomeFile,
            GeneInfo gene) throws IOException {

        String code;
        if (previousFile != null
                && previousFile.equals(chromosomeFile.getName())) {
            code = previousCode;
        } else {
            code = readCode(chromosomeFile);
            previousFile = chromosomeFile.getName();
            previousCode = code;
        }
        /*
         * if (codes.containsKey(chromosomeFile.getName())) { code =
         * codes.get(chromosomeFile.getName()); } else { code =
         * readCode(chromosomeFile); codes.put(chromosomeFile.getName(), code);
         * }
         */

        List<NucleotideSequence> parts = new ArrayList<NucleotideSequence>();
        List<GeneSequence> locations = gene.getLocations();

        for (GeneSequence location : locations) {
            // GFF (GenomeParserImpl) files use 1-based indices
            int startIndex = location.getStart() - 1;
            int stopIndex = location.getStop();

            if (!location.getSequenceType()) {
                parts.add(new NucleotideSequence(null, GeneSequence.INTRON,
                        location.getStart(), location.getStop()));
                continue;
            }

            StringBuilder sequence = new StringBuilder(code.substring(
                    startIndex, stopIndex));

            String sequenceString = GenomeConstant.REVERSE_FLAG.equals(gene
                    .getDirectionStr()) ? sequence.reverse().toString()
                    : sequence.toString();
            parts.add(new NucleotideSequence(sequenceString,
                    GeneSequence.CODING_SEQUENCE, location.getStart(), location
                            .getStop()));
        }
        if (GenomeConstant.REVERSE_FLAG.equals(gene.getDirectionStr())) {
            Collections.reverse(parts);
        }

        return parts;
    }
}
