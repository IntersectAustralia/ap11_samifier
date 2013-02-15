package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.ProteinLocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlimmerFileLocationGenerator implements LocationGenerator
{

    private String glimmerFilePath;

    public GlimmerFileLocationGenerator(String glimmerFilePath)
    {
        this.glimmerFilePath = glimmerFilePath;
    }

    @Override
    public List<ProteinLocation> generateLocations() throws LocationGeneratorException
    {
        File glimmerFile = new File(glimmerFilePath);
        List<ProteinLocation> locations = null;
        try
        {
            locations = parseGlimmerFile(glimmerFile);
        }
        catch (IOException e)
        {
            throw new LocationGeneratorException("Error with file " + glimmerFilePath, e);
        }

        Collections.sort(locations);
        return locations;
    }

    public List<ProteinLocation> parseGlimmerFile(File glimmerFile)
            throws LocationGeneratorException, IOException
    {
        int lineCount = 0;
        List<ProteinLocation> proteinLocations = new ArrayList<ProteinLocation>();
        for(String line : FileUtils.readLines(glimmerFile))
        {
            lineCount++;
            if (line.startsWith(">"))
            {
                continue;
            }
            String[] columns = line.split("\\s+");
            if (columns.length < 5)
            {
                throw new LocationGeneratorException("Expecting 5 columns at line: "+lineCount);
            }

            String name = columns[0];
            int firstIndex = Integer.parseInt(columns[1]);
            int secondIndex = Integer.parseInt(columns[2]);
            String direction = columns[3].substring(0,1);
            String frame = columns[3].substring(1);

            BigDecimal confidenceScore = new BigDecimal(columns[4]);

            if (direction.equals(GenomeConstant.FORWARD_FLAG))
            {
                proteinLocations.add(new ProteinLocation(name, firstIndex, secondIndex - firstIndex + 1, direction, frame, confidenceScore));
            }
            else if (direction.startsWith(GenomeConstant.REVERSE_FLAG))
            {
                proteinLocations.add(new ProteinLocation(name, secondIndex, firstIndex - secondIndex + 1, GenomeConstant.REVERSE_FLAG, frame, confidenceScore));
            }
            else
            {
                throw new LocationGeneratorException("Unexpected value for direction (4th column) at line: "+lineCount);
            }
        }
        return proteinLocations;
    }


}
