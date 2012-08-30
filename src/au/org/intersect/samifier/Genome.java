package au.org.intersect.samifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    public static Genome parse(File genomeFile)
        throws GenomeFileParsingException, FileNotFoundException, IOException
    {
        Genome genome = new Genome();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(genomeFile));
            
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.matches("^#.*$"))
                {
                    continue;
                }
                // chromosome, source, type, start, stop, score, strand, phase, attributes
                String[] parts = line.split("\\s+");
                if (parts.length < 9)
                {
                    // TODO: log error
                    continue;
                }
                String type = parts[2];
                if (type == null)
                {
                    continue;
                }
                Matcher typeMatcher = GeneSequence.SEQUENCE_RE.matcher(type);
                if (typeMatcher.matches())
                {
                    processSequence(parts, genome);
                }
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return genome;
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

    public Set<Map.Entry<String,GeneInfo>> getGenes()
    {
        return genes.entrySet();
    }

    public String toString()
    {
        StringBuffer out = new StringBuffer();
        for (Map.Entry<String, GeneInfo> entry : genes.entrySet())
        {
            out.append(entry.getKey());
            out.append(System.getProperty("line.separator"));
            out.append("\t");
            out.append(entry.getValue());
            out.append(System.getProperty("line.separator"));
        }
        return out.toString();
    }

    private static void processSequence(String[] parts, Genome genome)
    {
        String chromosome = parts[0];
        String type = parts[2];
        String orderedLocusName = getOrderedLocusName(parts[8]);
        int start = Integer.parseInt(parts[3]);
        String direction = parts[6];
        if ("gene".equals(type))
        {
            GeneInfo gene = new GeneInfo(chromosome, start, direction);
            genome.addGene(orderedLocusName, gene);
        }
        else
        {
            int stop = Integer.parseInt(parts[4]);
            GeneInfo gene = genome.getGene(orderedLocusName);
            if (gene == null)
            {
                System.err.println(orderedLocusName + " not found in genome object");
                return;
            }
            GeneSequence seq = new GeneSequence(type, start, stop, direction);
            gene.addLocation(seq);
        }
    }

    private static String getOrderedLocusName(String attributes)
    {
        Pattern olnPattern = Pattern.compile(".*Name=([^_;]+)[_;].*");
        Matcher m = olnPattern.matcher(attributes);
        if (m.matches())
        {
            return m.group(1);
        }
        return null;
    }
}
