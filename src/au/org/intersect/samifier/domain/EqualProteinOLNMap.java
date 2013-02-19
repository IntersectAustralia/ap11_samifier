package au.org.intersect.samifier.domain;

public class EqualProteinOLNMap implements ProteinToOLNMap {
    @Override
    public boolean containsProtein(String protein) {
        return true;
    }

    @Override
    public String getOLN(String protein) {
        return protein;
    }
}
