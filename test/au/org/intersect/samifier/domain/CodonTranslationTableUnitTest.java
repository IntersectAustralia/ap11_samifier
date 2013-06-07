package au.org.intersect.samifier.domain;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CodonTranslationTableUnitTest
{

    @Test
    public void testParseTableFile()
    {
        try {
            File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
            CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);
            assertEquals("ATT codes for Isoleucine", "I", table.toAminoAcid("ATT"));
            assertEquals("Should have 61 codons", 61, table.getCodons().length);
            assertEquals("Should have 3 start codons", 3, table.getStartCodons().length);
            assertEquals("Should have 3 stop codons", 3, table.getStopCodons().length);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testToStartAminoAcid()
    {
        try {
            File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
            CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);

            assertEquals("TTG maps to M", "M", table.toStartAminoAcid("TTG"));
            assertEquals("CTG maps to M", "M", table.toStartAminoAcid("TTG"));
            assertEquals("ATG maps to M", "M", table.toStartAminoAcid("TTG"));
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testToAminoAcid()
    {
        try {
            File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
            CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);

            assertEquals("A codon containing nucleotide W maps to X", "X", table.toAminoAcid("AAW"));
            assertEquals("A codon containing nucleotide S maps to X", "X", table.toAminoAcid("AAS"));
            assertEquals("A codon containing nucleotide M maps to X", "X", table.toAminoAcid("AAM"));
            assertEquals("A codon containing nucleotide K maps to X", "X", table.toAminoAcid("AAK"));
            assertEquals("A codon containing nucleotide R maps to X", "X", table.toAminoAcid("AAR"));
            assertEquals("A codon containing nucleotide Y maps to X", "X", table.toAminoAcid("AAY"));

            assertNull("An unknown codon returns null", table.toAminoAcid("ZZZ"));
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testNucleotideToAminoAcidSequence()
    {
        try {
            File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
            CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);

            // Stop codons need to be translated
            assertEquals("Stop codons are translated", "***", table.proteinToAminoAcidSequence("TAATAGTGA"));

            // All the sequences below start with the standard start codon ATG
            // Isoleucine
            assertEquals("3 codons code for Isoleucine", "MIII", table.proteinToAminoAcidSequence("ATGATTATCATA"));

            // Leucine
            assertEquals("6 codons code for Leucine", "MLLLLLL", table.proteinToAminoAcidSequence("ATGCTTCTCCTACTGTTATTG"));

            // Valine
            assertEquals("4 codons code for Valine", "MVVVV", table.proteinToAminoAcidSequence("ATGGTTGTCGTAGTG"));

            // Phenylalanine
            assertEquals("2 codons code for Phenylalanine", "MFF", table.proteinToAminoAcidSequence("ATGTTTTTC"));

            // Methionine
            assertEquals("1 codon codes for Methionine", "M", table.proteinToAminoAcidSequence("ATG"));

            // Cysteine
            assertEquals("2 codons code for Cysteine", "MCC", table.proteinToAminoAcidSequence("ATGTGTTGC"));

            // Arginine
            assertEquals("4 codons code for Arginine", "MAAAA", table.proteinToAminoAcidSequence("ATGGCTGCCGCAGCG"));

            // Proline
            assertEquals("4 codons code for Proline", "MPPPP", table.proteinToAminoAcidSequence("ATGCCTCCCCCACCG"));

            // Threonine
            assertEquals("4 codons code for Threonine", "MTTTT", table.proteinToAminoAcidSequence("ATGACTACCACAACG"));

            // Serine
            assertEquals("6 codons code for Serine", "MSSSSSS", table.proteinToAminoAcidSequence("ATGTCTTCCTCATCGAGTAGC"));

            // Tyrosine
            assertEquals("2 codons code for Tyrosine", "MYY", table.proteinToAminoAcidSequence("ATGTATTAC"));

            // Tryptophan
            assertEquals("1 codon codes for Tryptophan", "MW", table.proteinToAminoAcidSequence("ATGTGG"));

            // Glutamine
            assertEquals("2 codons code for Glutamine", "MQQ", table.proteinToAminoAcidSequence("ATGCAACAG"));

            // Asparagine
            assertEquals("2 codons code for Asparagine", "MNN", table.proteinToAminoAcidSequence("ATGAATAAC"));

            // Histidine
            assertEquals("2 codons code for Histidine", "MHH", table.proteinToAminoAcidSequence("ATGCATCAC"));

            // Glutamic acid
            assertEquals("2 codons code for Glutamic acid", "MEE", table.proteinToAminoAcidSequence("ATGGAAGAG"));

            // Aspartic acid 
            assertEquals("2 codons code for Aspartic acid", "MDD", table.proteinToAminoAcidSequence("ATGGATGAC"));

            // Lysine
            assertEquals("2 codons code for Lysine", "MKK", table.proteinToAminoAcidSequence("ATGAAAAAG"));

            // Arginine
            assertEquals("6 codons code for Arginine", "MRRRRRR", table.proteinToAminoAcidSequence("ATGCGTCGCCGACGGAGAAGG"));
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(expected = UnknownCodonException.class)
    public void testNucleotideToAminoAcidSequenceWithBadCodon()
        throws Exception
    {
        File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
        CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);
        table.proteinToAminoAcidSequence("ZZZ");
    }
    
    @Test
    public void testNucleotideToAminoAcidContainingN() throws Exception {
        File f = new File("test/resources/protein_generator/standard_code_translation_table.txt");
        CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("AAN"));
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("ANN"));
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("NNN"));
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("NAA"));
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("NNA"));
        assertEquals("Unknown aminacid should be returned", "X" , table.proteinToAminoAcidSequence("NAN"));
    }
}
