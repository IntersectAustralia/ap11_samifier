package au.org.intersect.samifier.domain;

public interface ProteinToOLNMap {
    boolean containsProtein(String protein);

    String getOLN(String protein);

}
