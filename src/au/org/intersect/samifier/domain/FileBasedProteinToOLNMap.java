package au.org.intersect.samifier.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class FileBasedProteinToOLNMap implements ProteinToOLNMap {
    private Map<String, String> proteinToOLNMap = new HashMap<String, String>();
    private static Logger LOG = Logger.getLogger(FileBasedProteinToOLNMap.class);
    @Override
    public boolean containsProtein(String protein) {
        return proteinToOLNMap.containsKey(protein);
    }

    @Override
    public String getOLN(String protein) {
        return proteinToOLNMap.get(protein);
    }

    public void addMapping(String protein, String OLN) {
        if (proteinToOLNMap.containsKey(protein)) {
            LOG.warn("ID " + protein + " is duplicated. Previous value was " + proteinToOLNMap.get(protein) + ". It will be replaced by " + OLN);
        }
        proteinToOLNMap.put(protein, OLN);
    }

}
