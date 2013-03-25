package au.org.intersect.samifier.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Genome {

    private Map<String, GeneInfo> genes;

    public Genome() {
        genes = new HashMap<String, GeneInfo>();
    }

    public void addGene(GeneInfo gene) {
        genes.put(gene.getId(), gene);
    }

    public boolean hasGene(String orderedLocusName) {
        return genes.containsKey(orderedLocusName);
    }

    public GeneInfo getGene(String orderedLocusName) {
        return genes.get(orderedLocusName);
    }

    public Set<Map.Entry<String, GeneInfo>> getGeneEntries() {
        return genes.entrySet();
    }

    public Collection<GeneInfo> getGenes() {
        return genes.values();
    }

    public Set<String> getLocusNames() {
        return genes.keySet();
    }

    public String toString() {
        StringBuffer out = new StringBuffer();
        for (String orderedLocusName : genes.keySet()) {
            GeneInfo geneInfo = genes.get(orderedLocusName);
            out.append(orderedLocusName);
            out.append(System.getProperty("line.separator"));
            out.append("\t");
            out.append(geneInfo);
            out.append(System.getProperty("line.separator"));
        }
        return out.toString();
    }

}
