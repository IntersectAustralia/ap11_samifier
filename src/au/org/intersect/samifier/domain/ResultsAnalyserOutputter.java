package au.org.intersect.samifier.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ResultsAnalyserOutputter {
    private static final String SEPARATOR = "\t";
    private static final String TABLENAME = "Result";
    private static final String DELIMITER = ",";

    private String proteinId;
    private String locusName;
    private String geneId;
    private String score;
    private String startPosition;
    private String stopPosition;
    private String lengthInAminoacids;
    private String chromosomeId;
    private String geneStart;
    private String geneEnd;
    private String strand;
    private String frame;
    private String exons;
    private String exonString;
    private String queryId; // / Change by Ignatius Pang *%*%*%
    private String validatedSequence; // / Change by Ignatius Pang *%*%*%
    private String peptideSequnce;
    private String fileName;
    private String comments;
    private boolean valid;
    private static Logger LOG = Logger.getLogger(ResultsAnalyserOutputter.class);
    public ResultsAnalyserOutputter(PeptideSearchResult peptideSearchResult,
            ProteinToOLNMap proteinToOLNMap, Genome genome,
            PeptideSequence peptideSequence) throws UnknownCodonException {
        this(peptideSearchResult, proteinToOLNMap, genome, peptideSequence, null);
    }

    public ResultsAnalyserOutputter(PeptideSearchResult peptideSearchResult,
            ProteinToOLNMap proteinToOLNMap, Genome genome,
            PeptideSequence peptideSequence,
            CodonTranslationTable translationTable) throws UnknownCodonException {
        this.valid = true;
        this.peptideSequnce = peptideSearchResult.getPeptideSequence();
        this.fileName = peptideSearchResult.getFileName();
        this.proteinId = peptideSearchResult.getProteinName();
        this.locusName = proteinToOLNMap.getOLN(peptideSearchResult
                .getProteinName());
        // This one needs to be confirmed
        this.geneId = proteinToOLNMap.getOLN(peptideSearchResult
                .getProteinName());
        this.score = peptideSearchResult.getConfidenceScore().toString();
        this.startPosition = Integer.toString(peptideSearchResult
                .getPeptideStart());
        this.stopPosition = Integer.toString(peptideSearchResult
                .getPeptideStop());
        this.lengthInAminoacids = Integer.toString(peptideSearchResult
                .getSequenceLength());

        GeneInfo geneInfo = genome.getGene(locusName);

        this.chromosomeId = geneInfo.getChromosome();
        this.geneStart = new Integer(geneInfo.getStart()).toString();
        this.geneEnd = new Integer(geneInfo.getStop()).toString();
        this.strand = getStrand(geneInfo);
        this.frame = getFrame(geneInfo);

        this.exons = new Integer(numberOfExons(peptideSequence.getCigarString())).toString();
        this.exonString = getExonString(peptideSequence, geneInfo);

        this.queryId = peptideSearchResult.getId(); // / Change by Ignatius Pang
        if (geneInfo.isFromVirtualProtein()) {
            GeneInfo newInfo = genome.getGene(geneInfo.getOriginalGeneId());
            long absoluteStart = Long.parseLong(this.geneStart) + (Long.parseLong(this.startPosition) - 1) * GenomeConstant.BASES_PER_CODON;
            long absoluteStop = absoluteStart + newInfo.getDirection() * peptideSearchResult.getPeptideSequence().length() * GenomeConstant.BASES_PER_CODON + (-1);
            this.proteinId = newInfo.getId();
            this.locusName = newInfo.getId();
            this.geneId = newInfo.getId();
            this.geneStart = Integer.toString(newInfo.getStart());
            this.geneEnd = Integer.toString(newInfo.getStop());
            long relativeStart = absoluteStart - Long.parseLong(this.geneStart);
            long reletiveStop = absoluteStop -  Long.parseLong(this.geneStart) + 1;
            this.startPosition = relativeStart < reletiveStop ? Long.toString(relativeStart) : Long.toString(reletiveStop);
            this.stopPosition = relativeStart < reletiveStop ? Long.toString(reletiveStop) : Long.toString(relativeStart);
            comments = geneInfo.getComments();
            String [] peptideGenomic = this.exonString.split("-");
            long peptideGenomicStart = Long.parseLong(peptideGenomic[0]);
            long peptideGenomicStop = Long.parseLong(peptideGenomic[1]);
            if (peptideGenomicStart < newInfo.getStart() || peptideGenomicStop > newInfo.getStop()
            || peptideGenomicStop < newInfo.getStart() || peptideGenomicStart > newInfo.getStop()
            || Long.parseLong(startPosition) < 0 || Long.parseLong(stopPosition) > Math.abs(newInfo.getStop() - newInfo.getStart())) {
                LOG.warn(" Search result " + peptideSearchResult + " from file "+ peptideSearchResult.getFileName() +" is invalid and will be removed.");
                valid = false;
            }
        }
        if (translationTable != null) {
            String nucleotideString = peptideSequence.getNucleotideSequence();
            int direction = peptideSequence.getGeneInfo().getDirection(); // getDirection()// == 1 ? "+" : "-";
            String mascotPeptideString = peptideSearchResult.getPeptideSequence();
            String predictedAminoAcidSequence = new String("");
            if (direction != 1) {
                StringBuilder invertedReversedSequence = new StringBuilder(
                        StringUtils.replaceChars(nucleotideString, "ACGT", "TGCA"))
                        .reverse();
                predictedAminoAcidSequence = translationTable
                        .proteinToAminoAcidSequence(invertedReversedSequence
                                .toString());
            } else {
                predictedAminoAcidSequence = translationTable
                        .proteinToAminoAcidSequence(nucleotideString);
            }
            if (predictedAminoAcidSequence.equals(mascotPeptideString)) {
                this.validatedSequence = new String("True");
            } else {
                this.validatedSequence = new String("False");
            }
        }
        //if gene is build based on vp we'll reverse that process and put vp data in comments
    }

    private String getExonString(PeptideSequence peptideSequence,
            GeneInfo geneInfo) {
        // cigar string example: 23M238N4M
        // first 23 are introns
        // then gap of 238
        // then next 4 are introns
        String cigar = peptideSequence.getCigarString();
        String[] sequenceLengths = cigar.split("N|M");
        StringBuilder exonString = new StringBuilder();

        int startPosition = geneInfo.getStart()
                + peptideSequence.getStartIndex();
        boolean intron = true;
        boolean firstOne = true;

        for (String sequenceLengthStr : sequenceLengths) {
            int sequenceLength = Integer.parseInt(sequenceLengthStr);
            int stopPosition = startPosition + sequenceLength - 1;
            if (intron) {
                if (firstOne) {
                    firstOne = false;
                } else {
                    exonString.append(":");
                }

                exonString.append(Integer.toString(startPosition));
                exonString.append("-");
                exonString.append(Integer.toString(stopPosition));
            }

            startPosition = stopPosition + 1;
            intron = !intron;
        }
        return exonString.toString();
    }

    private String getStrand(GeneInfo geneInfo) {
        int direction = geneInfo.getDirection();

        return (direction == 1 ? "+" : "-");
    }

    private String getFrame(GeneInfo geneInfo) {
        int offset = (geneInfo.getStart() - 1) % GenomeConstant.BASES_PER_CODON;
        return Integer.toString(offset);
    }

    private int numberOfExons(String cigarString) {
        return cigarString.split("N").length;
    }

    public String toString() {
        StringBuffer output = new StringBuffer();
        output.append(proteinId + SEPARATOR);
        output.append(locusName + SEPARATOR);
        output.append(geneId + SEPARATOR);
        output.append(score + SEPARATOR);
        output.append(startPosition + SEPARATOR);
        output.append(stopPosition + SEPARATOR);
        output.append(lengthInAminoacids + SEPARATOR);
        output.append(chromosomeId + SEPARATOR);
        output.append(geneStart + SEPARATOR);
        output.append(geneEnd + SEPARATOR);
        output.append(strand + SEPARATOR);
        output.append(frame + SEPARATOR);
        output.append(exons + SEPARATOR);
        output.append(exonString + SEPARATOR); // / Change by Ignatius Pang
                                               // *%*%*%
        output.append(queryId + SEPARATOR);
        output.append(peptideSequnce + SEPARATOR);
        output.append(fileName + SEPARATOR);
        if (DebuggingFlag.get_sbi_debug_flag() == 1) {// / Change by Ignatius
                                                     // Pang *%*%*%
            output.append(comments + SEPARATOR);
            output.append(validatedSequence);
        } else {
            output.append(comments); // / Change by Ignatius Pang *%*%*%
        }
        //
        return output.toString();
    }

    public String toQuery() {
        StringBuffer output = new StringBuffer();
        output.append("INSERT INTO " + TABLENAME + " (");

        if (DebuggingFlag.get_sbi_debug_flag() == 1) {
            output.append("proteinId,locusName,geneId,score,startPosition,stopPosition,lengthInAminoacids,chromosomeId,geneStart,geneEnd,strand,frame,exons,exonString,queryId,peptide_sequence,filename,comments,validatedSequence) ");
        } else {
            output.append("proteinId,locusName,geneId,score,startPosition,stopPosition,lengthInAminoacids,chromosomeId,geneStart,geneEnd,strand,frame,exons,exonString,queryId, peptide_sequence,filename, comments ) ");
        }
        output.append("VALUES (");
        output.append(formColumnQuery(proteinId) + DELIMITER);
        output.append(formColumnQuery(locusName) + DELIMITER);
        output.append(formColumnQuery(geneId) + DELIMITER);
        output.append(formColumnQuery(score) + DELIMITER);
        output.append(formColumnQuery(startPosition) + DELIMITER);
        output.append(formColumnQuery(stopPosition) + DELIMITER);
        output.append(formColumnQuery(lengthInAminoacids) + DELIMITER);
        output.append(formColumnQuery(chromosomeId) + DELIMITER);
        output.append(formColumnQuery(geneStart) + DELIMITER);
        output.append(formColumnQuery(geneEnd) + DELIMITER);
        output.append(formColumnQuery(strand) + DELIMITER);
        output.append(formColumnQuery(frame) + DELIMITER);
        output.append(formColumnQuery(exons) + DELIMITER);
        output.append(formColumnQuery(exonString) + DELIMITER); // / Change by
                                                                // Ignatius Pang
                                                                // *%*%*%
        output.append(formColumnQuery(queryId) + DELIMITER);
        output.append(formColumnQuery(peptideSequnce) + DELIMITER);
        output.append(formColumnQuery(fileName) + DELIMITER);
        if (DebuggingFlag.get_sbi_debug_flag() == 1) {
            output.append(formColumnQuery(comments) + DELIMITER);
            output.append(formColumnQuery(validatedSequence) + ");"); // /
                                                                      // Change
                                                                      // by
                                                                      // Ignatius
                                                                      // Pang
                                                                      // *%*%*%
        } else {
            output.append(formColumnQuery(comments) +  ")");
            
        }

        return output.toString();
    }

    private String formColumnQuery(String field) {
        return "'" + field + "'";
    }

    public boolean isValid() {
        return valid;
    }
}
