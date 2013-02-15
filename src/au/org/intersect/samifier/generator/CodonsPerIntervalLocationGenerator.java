package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.ProteinLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodonsPerIntervalLocationGenerator implements LocationGenerator
{
    private String interval;
    private File genomeFile;

    public CodonsPerIntervalLocationGenerator(String interval, File genomeFile)
    {
        this.interval = interval;
        this.genomeFile = genomeFile;
    }

    @Override
    public List<ProteinLocation> generateLocations() throws LocationGeneratorException
    {
        int codonsPerInterval = Integer.parseInt(interval);
        try
        {
            return createLocations(genomeFile, codonsPerInterval);
        }
        catch (IOException e)
        {
            throw new LocationGeneratorException("Could not generate locations as codons per interval", e);
        }
    }

    public List<ProteinLocation> createLocations(File genomeFile, int codonsPerInterval)
            throws IOException, FileNotFoundException
    {
        BufferedReader reader = new BufferedReader(new FileReader(genomeFile));
        int baseCount = 0;
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            if (line.matches("^>.*$"))
            {
                continue;
            }

            baseCount += StringUtils.chomp(line).length();
        }

        int basesPerInterval = codonsPerInterval * GenomeConstant.BASES_PER_CODON;
        if (basesPerInterval >= baseCount)
        {
            // TODO: log this to error file
            return null;
        }

        List<ProteinLocation> locations = new ArrayList<ProteinLocation>();
        int nameIndex = 0;
        int halfIntervalSize = basesPerInterval / 2;
        int lastCodonStartPosition = baseCount - GenomeConstant.BASES_PER_CODON;

        // Forward locations
        for (int i=1; i <= baseCount; i += basesPerInterval)
        {
            addLocations(locations, i, nameIndex, basesPerInterval, baseCount, true, false);
            addLocations(locations, i, nameIndex, basesPerInterval, baseCount, false, false);
            int halfIntervalStart = i + halfIntervalSize;
            if (halfIntervalStart <= lastCodonStartPosition)
            {
                addLocations(locations, halfIntervalStart, nameIndex, basesPerInterval, baseCount, true, true);
                addLocations(locations, halfIntervalStart, nameIndex, basesPerInterval, baseCount, false, true);
            }
            nameIndex++;
        }

        // Reverse locations
        /*for (int i=baseCount; i > 0; i -= basesPerInterval)
        {
            int start = i - basesPerInterval;
            addLocations(locations, start, nameIndex, basesPerInterval, baseCount, false, false);
            int halfIntervalStart = start - halfIntervalSize;
            int halfIntervalEnd = halfIntervalStart + basesPerInterval;
            if (halfIntervalEnd > 0)
            {
                addLocations(locations, halfIntervalStart, nameIndex, basesPerInterval, baseCount, false, true);
            }
            nameIndex++;
        } */
        return locations;
    }

    private void addLocations(List<ProteinLocation> locations, int start, int nameIndex, int basesPerInterval, int baseCount, boolean isForward, boolean isHalfInterval)
            throws IOException
    {
        boolean oddNumberOfBases = basesPerInterval % 2 == 1;

        // 3 frame translation (see http://en.wikipedia.org/wiki/Reading_frame)
        for (int subIndex=0; subIndex < 3; subIndex++)
        {
            int startIndex = start;
            if (oddNumberOfBases && isHalfInterval)
            {
                startIndex += isForward ? -1 : 1;
            }

            int endIndex = startIndex + basesPerInterval - 1;
           // if (isForward)
           // {
                startIndex += subIndex;
                endIndex   += subIndex;
          //  }
          //  else
          //  {
          //      startIndex -= subIndex;
          //      endIndex   -= subIndex;
          //  }

            // Ensure the start and end positions are a multiple of 3.
            // i.e. a full codon
            if (startIndex <= 0)
            {
                int shiftFactor = endIndex % GenomeConstant.BASES_PER_CODON;
                startIndex = 1 + shiftFactor;
                //endIndex += shiftFactor;
            }
            if (endIndex > baseCount)
            {
                int leftOverBases = (baseCount - startIndex + 1) % GenomeConstant.BASES_PER_CODON;
                endIndex = baseCount - leftOverBases;
            }

            if (startIndex >= endIndex)
            {
                continue;
            }

            String name = "p" +
                    nameIndex +
                    (isHalfInterval ? "b" : "a") +
                    "."  +
                    (isForward ? "+" : "-") +
                    (subIndex+1);
            int length = endIndex - startIndex + 1;
            String frame = Integer.toString(subIndex + 1);
            locations.add(new ProteinLocation(name, startIndex, length, isForward ? GenomeConstant.FORWARD_FLAG : GenomeConstant.REVERSE_FLAG, frame));
        }
    }


}
