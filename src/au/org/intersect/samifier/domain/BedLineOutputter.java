package au.org.intersect.samifier.domain;

public class BedLineOutputter {
    private String chromosomeName;
    private int geneStart;
    private int geneStop;
    private String proteinName;

    public BedLineOutputter(PeptideSequence peptide, String proteinName) {
        this.chromosomeName = peptide.getGeneInfo().getChromosome();
        this.geneStart = peptide.getGeneInfo().getStart();
        this.geneStop = peptide.getGeneInfo().getStop();
        this.proteinName = proteinName;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(chromosomeName).append("\t");
        output.append(geneStart).append("\t");
        output.append(geneStop).append("\t");
        output.append(proteinName);
        output.append(System.getProperty("line.separator"));

        return output.toString();
    }

}
