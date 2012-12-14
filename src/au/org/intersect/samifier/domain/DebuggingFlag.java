package au.org.intersect.samifier.domain;

public class DebuggingFlag {

    /// Change by Ignatius Pang  *%*%*%
    /// This debug flag is currently set to provide internal validation alternatively spliced peptides. 
    /// The nucleotide sequence in the 'output' SAM file is compared with the amino acid sequence in the 'input' Mascot DAT or mzIdentML file.
	private static int sbi_debug = 1;
	
	
	public static int get_sbi_debug_flag()
		{ 
			return sbi_debug;
				
		}
}
