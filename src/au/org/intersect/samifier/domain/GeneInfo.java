package au.org.intersect.samifier.domain;

import au.org.intersect.samifier.Samifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneInfo {
    private String chromosome;
    private String id;
    private int start;
    private int stop;
    private int direction;
    private List<GeneSequence> locations;
    private Map<String, VirtualProtein> virtualProteins;
    private boolean fromVirtualProtein;
    private String comments;
    private String originalGeneId;
    
    public GeneInfo() {
        // locations = new TreeSet(new GeneSequenceComparator());
        locations = new ArrayList<GeneSequence>();
    }

    public GeneInfo(String chromosome, String id, int start, int stop, int direction) {
        this(chromosome, id, start, stop, direction, null);
    }
    public GeneInfo(String chromosome, String id, int start, int stop, int direction, List<VirtualProtein> virtualProteins) {
        // this(chromosome, start, direction, new TreeSet(new
        // GeneSequenceComparator()));
        this(chromosome, id, start, stop, direction, new ArrayList<GeneSequence>(), virtualProteins);
    }

    private GeneInfo(String chromosome, String id, int start, int stop, int direction, List<GeneSequence> locations, List<VirtualProtein> virtualProteins) {
        setChromosome(chromosome);
        setId(id);
        setStart(start);
        setStop(stop);
        setDirection(direction);
        this.locations = locations;
        if (virtualProteins != null && virtualProteins.size() > 0 ) {
            this.virtualProteins = new HashMap<String, VirtualProtein>();
            for (VirtualProtein vp: virtualProteins) {
                this.virtualProteins.put(vp.getName(), vp);
            }
        }
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public int getStop() {
        return stop;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public int getDirectionFlag() {
        if (GenomeConstant.REVERSE_FLAG.equals(getDirectionStr())) {
            return Samifier.SAM_REVERSE_FLAG;
        } else {
            return 0;
        }
    }

    public String getDirectionStr() {
        return getDirection() == 1 ? "+" : "-";
    }

    public void addLocation(GeneSequence location) {
        locations.add(location);
    }

    public void setLocations(List<GeneSequence> locations) {
        this.locations = locations;
    }

    public List<GeneSequence> getLocations() {
        Collections.sort(locations, new GeneSequenceComparator());
        return locations;
    }

    public String toString() {
        return "chromosome: " + chromosome + ", start: " + start
                + ", direction: " + direction;
    }

    public boolean isForward() {
        return getDirection() == 1;
    }

    public boolean hasVirtualProtein(String orderedLocusName) {
        if (virtualProteins == null) return false;
        return virtualProteins.containsKey(orderedLocusName);
    }

    public VirtualProtein getVirtualProtein(String vpName) {
        return virtualProteins.get(vpName);
    }

    public boolean isFromVirtualProtein() {
        return fromVirtualProtein;
    }

    public void setFromVirtualProtein(boolean fromVirtualProtein) {
        this.fromVirtualProtein = fromVirtualProtein;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getOriginalGeneId() {
        return originalGeneId;
    }

    public void setOriginalGeneId(String originalGeneId) {
        this.originalGeneId = originalGeneId;
    }
}
