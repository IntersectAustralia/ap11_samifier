package au.org.intersect.samifier.tool;

import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.GenomeNucleotides;
import au.org.intersect.samifier.domain.UnknownCodonException;

import java.io.File;

public class CodonFinder
{
    private static GenomeNucleotides nucleotides;
    private static CodonTranslationTable translationTable;

    public static void main(String[] args) throws Exception
    {
        try
        {
            File fastaFile = new File(args[0]);
            File translationTableFile = new File(args[1]);
            int direction = "+".equals(args[2]) ? 1 : -1;
            nucleotides = new GenomeNucleotides(fastaFile);
            translationTable = CodonTranslationTable.parseTableFile(translationTableFile);

            if (args.length == 4)
            {
                int codonPosition = Integer.parseInt(args[3]);
                printCodonAt(codonPosition, direction);
            }
            else if (args.length == 5)
            {
                int startCodonPosition = Integer.parseInt(args[3]);
                int endCodonPosition = Integer.parseInt(args[4]);
                printCodonRange(startCodonPosition, endCodonPosition, direction);
            }
            else if (args.length == 3)
            {
                int startCodonPosition = 1;
                int endCodonPosition = nucleotides.getSize()-2;
                printCodonRange(startCodonPosition, endCodonPosition, direction);
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(1);
        }

    }

    private static void printCodonRange(int startCodonPosition, int endCodonPosition, int direction) throws UnknownCodonException
    {
        for (int codonPosition = startCodonPosition; codonPosition <= endCodonPosition; codonPosition += GenomeConstant.BASES_PER_CODON)
        {
            printCodonAt(codonPosition, direction);
        }
    }

    private static void printCodonAt(int codonPosition, int direction) throws UnknownCodonException
    {
        String codon = nucleotides.codonAt(codonPosition, direction);
        System.out.print(codon);
        if (translationTable.isStartCodon(codon))
        {
            System.out.println("{" + translationTable.proteinToAminoAcidSequence(codon) + ":start}");
        }
        else if (translationTable.isStopCodon(codon))
        {
            System.out.println("{" + translationTable.proteinToAminoAcidSequence(codon) + ":stop}");
        }
        else
        {
            System.out.println("{" + translationTable.proteinToAminoAcidSequence(codon) + "}");
        }
    }
}