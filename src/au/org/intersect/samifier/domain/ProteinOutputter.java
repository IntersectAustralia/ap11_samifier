package au.org.intersect.samifier.domain;

import org.apache.commons.lang3.StringUtils;

public class ProteinOutputter implements Outputter {
    public static final int FASTA_LINE_LENGTH = 60;

    private String fastaHeader;
    private ProteinLocation proteinLocation;
    private StringBuilder genomeString;
    private CodonTranslationTable translationTable;

    public ProteinOutputter(ProteinLocation proteinLocation,
            String databaseName, StringBuilder genomeString,
            CodonTranslationTable translationTable) {
        this.fastaHeader = ">gn1|" + databaseName + "|"
                + proteinLocation.getName();
        this.proteinLocation = proteinLocation;
        this.genomeString = genomeString;
        this.translationTable = translationTable;
    }

    @Override
    public String getOutput() throws OutputException {
        String lineFeed = System.getProperty("line.separator");

        StringBuilder buffer = new StringBuilder();
        buffer.append(fastaHeader);
        buffer.append(lineFeed);

        int startIndex = proteinLocation.getStartIndex() - 1;
        int stopIndex = startIndex + proteinLocation.getLength();
        String sequence = genomeString.substring(startIndex, stopIndex);

        String aminoAcidSequence = null;
        if (proteinLocation.getDirection().equals(GenomeConstant.REVERSE_FLAG)) {
            StringBuilder invertedReversedSequence = new StringBuilder(
                    invertNucleotideSequence(sequence.toString())).reverse();
            aminoAcidSequence = translationTable
                    .proteinToAminoAcidSequence(invertedReversedSequence
                            .toString());
        } else {
            aminoAcidSequence = translationTable
                    .proteinToAminoAcidSequence(sequence.toString());
        }
        int sequenceLength = aminoAcidSequence.length();
        int wholeParts = sequenceLength / FASTA_LINE_LENGTH;
        int sequenceCursor = 0;
        for (int i = 0; i < wholeParts; i++) {
            buffer.append(aminoAcidSequence.substring(sequenceCursor,
                    sequenceCursor + FASTA_LINE_LENGTH));
            buffer.append(lineFeed);
            sequenceCursor += FASTA_LINE_LENGTH;
        }
        if (sequenceCursor < sequenceLength) {
            buffer.append(aminoAcidSequence.substring(sequenceCursor,
                    sequenceLength));
            buffer.append(lineFeed);
        }

        return buffer.toString();
    }

    public static String invertNucleotideSequence(String sequence) {
        return StringUtils.replaceChars(sequence, "ACGT", "TGCA");
    }

}
