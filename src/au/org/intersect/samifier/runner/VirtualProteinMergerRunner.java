package au.org.intersect.samifier.runner;

import au.org.intersect.samifier.domain.*;
import au.org.intersect.samifier.generator.LocationGenerator;
import au.org.intersect.samifier.generator.VirtualProteinMascotLocationGenerator;
import au.org.intersect.samifier.util.ProteinLocationFileGenerator;

import java.io.File;
import java.io.Writer;
import java.util.List;

public class VirtualProteinMergerRunner
{
    private String[] searchResultsPaths;
    private File genomeFile;
    private File translationTableFile;
    private File chromosomeDir;
    private Writer outputFile;


    public VirtualProteinMergerRunner(String[] searchResultsPaths, File translationTableFile, File genomeFile, File chromosomeDir, Writer outputFile)
    {
        this.searchResultsPaths = searchResultsPaths;
        this.genomeFile = genomeFile;
        this.chromosomeDir = chromosomeDir;
        this.translationTableFile = translationTableFile;
        this.outputFile = outputFile;
    }

    public void run() throws Exception
    {
        LocationGenerator locationGenerator = new VirtualProteinMascotLocationGenerator(searchResultsPaths, translationTableFile, genomeFile, chromosomeDir);
        List<ProteinLocation> locations = locationGenerator.generateLocations();
        String genomeFileName = genomeFile.getName();
        GffOutputterGenerator outputterGenerator = new GffOutputterGenerator(genomeFileName);
        ProteinLocationFileGenerator.generateFile(locations, outputFile, outputterGenerator);
    }
}
