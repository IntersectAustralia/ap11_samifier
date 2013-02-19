package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.ProteinLocation;

import java.util.List;

public interface LocationGenerator {

     /*
      * @return a list of the protein locations
     */
    List<ProteinLocation> generateLocations() throws LocationGeneratorException;
}
