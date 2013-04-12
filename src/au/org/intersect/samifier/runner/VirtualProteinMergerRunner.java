package au.org.intersect.samifier.runner;

import java.io.File;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import au.org.intersect.samifier.domain.GffOutputterGenerator;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.generator.VirtualProteinMascotLocationGenerator;
import au.org.intersect.samifier.util.ProteinLocationFileGenerator;

public class VirtualProteinMergerRunner {
    private String[] searchResultsPaths;
    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private Writer outputFile;

    public VirtualProteinMergerRunner(String[] searchResultsPaths,
            File translationTableFile, File genomeFile, File chromosomeDir,
            Writer outputFile) {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.chromosomeDir = chromosomeDir;
        this.translationTableFile = translationTableFile;
        this.outputFile = outputFile;
    }

    public void run() throws Exception {
        VirtualProteinMascotLocationGenerator locationGenerator = new VirtualProteinMascotLocationGenerator(
                searchResultsPaths, translationTableFile, genomeFile,
                chromosomeDir);
        List<ProteinLocation> locations = locationGenerator.generateLocations();
        Collections.sort(locations);
        GffOutputterGenerator outputterGenerator = new GffOutputterGenerator(genomeFile.getName());
        ProteinLocationFileGenerator.generateFile(locations, outputFile, outputterGenerator, "##gff-version 3");
    }
}
