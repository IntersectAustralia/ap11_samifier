package au.org.intersect.samifier.domain;

public class DebuggingFlag {

    // / Change by Ignatius Pang *%*%*%
    // / This debug flag is currently set to provide internal validation
    // alternatively spliced peptides.
    // / The nucleotide sequence in the 'output' SAM file is compared with the
    // amino acid sequence in the 'input' Mascot DAT or mzIdentML file.
    private static int sbi_debug = 0;

    public static int get_sbi_debug_flag() {
        return sbi_debug;

    }

    // / Decides whether the SAM file will use the Mascot score for the MAPQ
    // field, which denotes mapping quality.
    // / Value of 1 means use Mascot score, any other value debugging flag value
    // will make MAPQ = 255.
    private static int use_mascot_score = 0;

    public static int get_use_mascot_score_flag() {
        return use_mascot_score;

    }

}
