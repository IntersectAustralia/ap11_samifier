package au.org.intersect.samifier.domain;

import java.util.HashMap;
import java.util.Map;

public class FileBasedProteinToOLNMap implements ProteinToOLNMap {
    private Map<String, String> proteinToOLNMap = new HashMap<String, String>();

    @Override
    public boolean containsProtein(String protein) {
        return proteinToOLNMap.containsKey(protein);
    }

    @Override
    public String getOLN(String protein) {
        return proteinToOLNMap.get(protein);
    }

    public void addMapping(String protein, String OLN) {
        proteinToOLNMap.put(protein, OLN);
    }

}
