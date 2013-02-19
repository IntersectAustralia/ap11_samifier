package au.org.intersect.samifier.tool;

import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.ProteinOutputter;
import java.io.File;

public class NucleotideToAminoacid {
    public static void main(String[] args) {
        try {
            File f = new File(args[0]);
            CodonTranslationTable table = CodonTranslationTable
                    .parseTableFile(f);
            // frame
            String protein = args[1];
            if (args.length > 2) {
                int frame = Integer.parseInt(args[2]);
                if (frame > 1) {
                    protein = protein.substring(frame - 1);
                } else if (frame < 0) {
                    protein = (new StringBuilder(protein)).reverse().toString();
                    if (frame < -1) {
                        protein = protein.substring(Math.abs(frame) - 1);
                    }
                    int modulo = protein.length() % 3;
                    if (modulo != 0) {
                        protein = protein.substring(0, protein.length()
                                - modulo);
                    }
                    protein = ProteinOutputter
                            .invertNucleotideSequence(protein);
                }
            }
            int modulo = protein.length() % 3;
            if (modulo != 0) {
                protein = protein.substring(0, protein.length() - modulo);
            }
            System.out.println(table.proteinToAminoAcidSequence(protein));
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
