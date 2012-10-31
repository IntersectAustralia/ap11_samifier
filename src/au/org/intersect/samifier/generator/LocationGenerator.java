package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.ProteinLocation;

import java.util.List;

public interface LocationGenerator
{
    /**
     * returns a list of the protein locations
     * @return a list of the protein locations
     */
    public List<ProteinLocation> generateLocations() throws LocationGeneratorException;
}
