package au.org.intersect.samifier.tool;

import au.org.intersect.samifier.domain.CodonTranslationTable;

import java.io.File;

public class NucleotideToAminoacid
{
    public static void main(String[] args)
    {
        try
        {
            File f = new File(args[0]);
            CodonTranslationTable table = CodonTranslationTable.parseTableFile(f);
            System.out.println(table.proteinToAminoAcidSequence(args[1]));
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
}
