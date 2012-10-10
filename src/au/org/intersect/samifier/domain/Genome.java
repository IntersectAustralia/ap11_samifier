package au.org.intersect.samifier.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Genome
{
    private Map<String, GeneInfo> genes;

    public Genome()
    {
        genes = new HashMap<String, GeneInfo>();
    }

    public void addGene(String orderedLocusName, GeneInfo gene)
    {
        genes.put(orderedLocusName, gene);
    }

    public boolean hasGene(String orderedLocusName)
    {
        return genes.containsKey(orderedLocusName);
    }

    public GeneInfo getGene(String orderedLocusName)
    {
        return genes.get(orderedLocusName);
    }

    public Set<Map.Entry<String, GeneInfo>> getGenes()
    {
        return genes.entrySet();
    }

    public String toString()
    {
        StringBuffer out = new StringBuffer();
        for (String orderedLocusName : genes.keySet())
        {
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
