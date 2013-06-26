package au.org.intersect.samifier.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.parser.FastaParser;
import au.org.intersect.samifier.parser.FastaParserException;

public class CodonsPerIntervalLocationGenerator implements LocationGenerator {
    private String interval;
    //private File genomeFile;
    private FastaParser fastaParser;
    public CodonsPerIntervalLocationGenerator(String interval, FastaParser fastaParser) {
        this.interval = interval;
        this.fastaParser = fastaParser;
    }

    @Override
    public List<ProteinLocation> generateLocations()
            throws LocationGeneratorException {
        int codonsPerInterval = Integer.parseInt(interval);
        
        try {
            
            List<String> allChromosomes = fastaParser.scanForChromosomes();
            //chromosome = FilenameUtils.removeExtension(genomeFile.getName());
            List<ProteinLocation> locations = new ArrayList<ProteinLocation>();
            for (String chromosome : allChromosomes) {
                locations.addAll(createLocations(fastaParser.getChromosomeLength(chromosome), codonsPerInterval, chromosome));
            }
            Collections.sort(locations);
            return locations;
        } catch (IOException e) {
            throw new LocationGeneratorException(
                    "Could not generate locations as codons per interval", e);
        } catch (FastaParserException ex) {
            throw new LocationGeneratorException(
                    "Could not generate locations as codons per interval", ex);
        }
    }

    public List<ProteinLocation> createLocations(int chromosomeLength, int codonsPerInterval, String chromosome) throws IOException {
        boolean createHalfInterval = true;
        int basesPerInterval = codonsPerInterval
                * GenomeConstant.BASES_PER_CODON;
        if (basesPerInterval >= chromosomeLength) {
            // TODO: log this to error file
           // reader.close();
            basesPerInterval = chromosomeLength / GenomeConstant.BASES_PER_CODON;
            basesPerInterval = basesPerInterval * GenomeConstant.BASES_PER_CODON;
            createHalfInterval = false;
            
        }

        List<ProteinLocation> locations = new ArrayList<ProteinLocation>();
        int nameIndex = 0;
        int halfIntervalSize = basesPerInterval / 2;
        int lastCodonStartPosition = chromosomeLength - GenomeConstant.BASES_PER_CODON;

        // Forward locations
        for (int i = 1; i <= chromosomeLength; i += basesPerInterval) {
            addLocations(locations, i, nameIndex, basesPerInterval, chromosomeLength,
                    true, false, chromosome);
            addLocations(locations, i, nameIndex, basesPerInterval, chromosomeLength,
                    false, false, chromosome);
            int halfIntervalStart = i + halfIntervalSize;
            if (createHalfInterval && halfIntervalStart <= lastCodonStartPosition) {
                addLocations(locations, halfIntervalStart, nameIndex,
                        basesPerInterval, chromosomeLength, true, true, chromosome);
                addLocations(locations, halfIntervalStart, nameIndex,
                        basesPerInterval, chromosomeLength, false, true, chromosome);
            }
            nameIndex++;
        }

        //reader.close();
        return locations;
    }

    private void addLocations(List<ProteinLocation> locations, int start,
            int nameIndex, int basesPerInterval, int baseCount,
            boolean isForward, boolean isHalfInterval, String chromosome) throws IOException {
        boolean oddNumberOfBases = basesPerInterval % 2 == 1;

        // 3 frame translation (see http://en.wikipedia.org/wiki/Reading_frame)
        for (int subIndex = 0; subIndex < 3; subIndex++) {
            int startIndex = start;
            if (oddNumberOfBases && isHalfInterval) {
                startIndex += -1;
            }

            int endIndex = startIndex + basesPerInterval - 1;
            startIndex += subIndex;
            endIndex += subIndex;
            // Ensure the start and end positions are a multiple of 3.
            // i.e. a full codon
            if (startIndex <= 0) {
                int shiftFactor = endIndex % GenomeConstant.BASES_PER_CODON;
                startIndex = 1 + shiftFactor;
            }
            if (endIndex > baseCount) {
                int leftOverBases = (baseCount - startIndex + 1)
                        % GenomeConstant.BASES_PER_CODON;
                endIndex = baseCount - leftOverBases;
            }

            if (startIndex >= endIndex) {
                continue;
            }

            String name = "p" + nameIndex + (isHalfInterval ? "b" : "a") + "."
                    + (isForward ? "+" : "-") + (subIndex + 1);
            int length = endIndex - startIndex + 1;
            String frame = Integer.toString(subIndex + 1);
            ProteinLocation location = new ProteinLocation(name, startIndex, length,
                    isForward ? GenomeConstant.FORWARD_FLAG
                            : GenomeConstant.REVERSE_FLAG, frame, null, null, chromosome);
            location.setOrigin("VPGenerator");
            locations.add(location);
        }
    }

}
