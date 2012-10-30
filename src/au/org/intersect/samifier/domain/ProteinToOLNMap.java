package au.org.intersect.samifier.domain;

public interface ProteinToOLNMap
{
    public boolean containsProtein(String protein);

    public String getOLN(String protein);

}
