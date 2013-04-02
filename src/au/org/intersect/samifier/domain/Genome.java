package au.org.intersect.samifier.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import au.org.intersect.samifier.parser.GenomeFileParsingException;

public class Genome {

    private Map<String, GeneInfo> genes;
    private static Logger LOG = Logger.getLogger(Genome.class);
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

    public void verify() throws GenomeFileParsingException{
        for (GeneInfo genInfo : genes.values()) {
            //first we'll sort locations
            int start = genInfo.getStart();
            List <GeneSequence> newEntries = new ArrayList<GeneSequence>();
            for (GeneSequence sequence : genInfo.getLocations()) {
                if (sequence.getStart() < start) {
                    String errorMessage = "Gene " + genInfo.getId() + " in chromosome " +  genInfo.getChromosome() + " has overlaping part at position (" + sequence.getStart() +","+ start + "). Cannot continue.";
                    LOG.error(errorMessage);
                    throw new GenomeFileParsingException(errorMessage);
                }
                if (sequence.getStart() != start) {
                    LOG.warn("Gene " + genInfo.getId() + " in chromosome " +  genInfo.getChromosome() + " has missing part at position (" + start +","+(sequence.getStart() - 1)+ "). Assuming non coding sequence.");
                    GeneSequence seq = new GeneSequence(sequence.getParentId(), false, start, sequence.getStart() - 1, sequence.getDirection());
                    newEntries.add(seq);
                }
                start = sequence.getStop() + 1;
            }
            for (GeneSequence location : newEntries) {
                genInfo.addLocation(location);
            }
        }
    }


}
