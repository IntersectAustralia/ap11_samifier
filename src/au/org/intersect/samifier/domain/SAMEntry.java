package au.org.intersect.samifier.domain;

/**
 * See http://genome.sph.umich.edu/wiki/SAM
 */
public class SAMEntry {
    private String qname;
    private int flag = 0; // See
                          // http://picard.sourceforge.net/explain-flags.html
    private String rname;
    private int pos;
    private int mapq = 255;
    private String cigarString;
    private String rnext = "*";
    private int pnext = 0;
    private int tlen = 0;
    private String peptideSequence;
    private String qual = "*";
    private int chromosomeLength;

    public SAMEntry(String qname, GeneInfo gene, int pos, String cigarString,
            String peptideSequence) {
        this(qname, gene.getDirectionFlag(), gene.getChromosome(), pos, 255,
                cigarString, "*", 0, 0, peptideSequence, "*");
    }

    public SAMEntry(String qname, GeneInfo gene, int pos, String cigarString,
            String peptideSequence, int mapq) {
        this(qname, gene.getDirectionFlag(), gene.getChromosome(), pos, mapq,
                cigarString, "*", 0, 0, peptideSequence, "*");
    }

    private SAMEntry(String qname, int flag, String rname, int pos, int mapq,
            String cigarString, String rnext, int pnext, int tlen,
            String peptideSequence, String qual) {
        this.qname = qname;
        this.flag = flag;
        this.rname = rname;
        this.pos = pos;
        this.mapq = mapq;
        this.cigarString = cigarString;
        this.rnext = rnext;
        this.pnext = pnext;
        this.tlen = tlen;
        this.peptideSequence = peptideSequence;
        this.qual = qual;
    }

    public void setQname(String qname) {
        this.qname = qname;
    }

    public String getQname() {
        return qname;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getRname() {
        return rname;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setMapq(int mapq) {
        this.mapq = mapq;
    }

    public int getMapq() {
        return mapq;
    }

    public void setCigarString(String cigarString) {
        this.cigarString = cigarString;
    }

    public String getCigarString() {
        return cigarString;
    }

    public void setRnext(String rnext) {
        this.rnext = rnext;
    }

    public String getRnext() {
        return rnext;
    }

    public void getTlen(int tlen) {
        this.tlen = tlen;
    }

    public int getTlen() {
        return tlen;
    }

    public void setPeptideSequence(String peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public void getQual(String qual) {
        this.qual = qual;
    }

    public String getQual() {
        return qual;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(qname).append("\t");
        out.append(flag).append("\t");
        out.append(rname).append("\t");
        out.append(pos).append("\t");
        out.append(mapq).append("\t");
        out.append(cigarString).append("\t");
        out.append(rnext).append("\t");
        out.append(pnext).append("\t");
        out.append(tlen).append("\t");
        out.append(peptideSequence).append("\t");
        out.append(qual);
        out.append(System.getProperty("line.separator"));
        return out.toString();
    }

    public int getChromosomeLength() {
        return chromosomeLength;
    }

    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

}
