package au.org.intersect.samifier.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import au.org.intersect.samifier.domain.AccessionOutputterGenerator;
import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.GffOutputterGenerator;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.domain.ProteinOutputterGenerator;
import au.org.intersect.samifier.domain.UnknownCodonException;
import au.org.intersect.samifier.generator.CodonsPerIntervalLocationGenerator;
import au.org.intersect.samifier.generator.GlimmerFileLocationGenerator;
import au.org.intersect.samifier.generator.LocationGenerator;
import au.org.intersect.samifier.util.ProteinLocationFileGenerator;

public class ProteinGeneratorRunner {

    private String glimmerFilePath;
    private File genomeFile;
    private String interval;
    private String databaseName;
    private Writer outputWriter;
    private File translationTableFile;
    private Writer gffWriter;
    private Writer accessionWriter;

    public ProteinGeneratorRunner(String glimmerFilePath, File genomeFile,
            String interval, String databaseName, Writer outputWriter,
            File translationTableFile, Writer gffWriter, Writer accessionWriter) {
        this.glimmerFilePath = glimmerFilePath;
        this.genomeFile = genomeFile;
        this.interval = interval;
        this.databaseName = databaseName;
        this.outputWriter = outputWriter;
        this.translationTableFile = translationTableFile;
        this.gffWriter = gffWriter;
        this.accessionWriter = accessionWriter;
    }

    public void run() throws Exception {
        LocationGenerator locationGenerator = createLocationGenerator();
        List<ProteinLocation> locations = locationGenerator.generateLocations();
        generateProteinsFile(locations,
                CodonTranslationTable.parseTableFile(translationTableFile));
        generateGffFile(locations);
        generateAccessionFile(locations);
    }

    private LocationGenerator createLocationGenerator() {
        LocationGenerator locationGenerator;
        if (glimmerFilePath != null) {
            locationGenerator = new GlimmerFileLocationGenerator(
                    glimmerFilePath);
        } else {
            locationGenerator = new CodonsPerIntervalLocationGenerator(
                    interval, genomeFile);
        }
        return locationGenerator;
    }

    private void generateGffFile(List<ProteinLocation> locations)
            throws IOException {
        String genomeFileName = genomeFile.getName();
        GffOutputterGenerator outputterGenerator = new GffOutputterGenerator(
                genomeFileName);
        ProteinLocationFileGenerator.generateFile(locations, gffWriter,
                outputterGenerator, "##gff-version 3");
    }

    private void generateAccessionFile(List<ProteinLocation> locations)
            throws IOException {
        AccessionOutputterGenerator outputterGenerator = new AccessionOutputterGenerator();
        ProteinLocationFileGenerator.generateFile(locations, accessionWriter,
                outputterGenerator);
    }

    public void generateProteinsFile(List<ProteinLocation> locations,
            CodonTranslationTable table) throws IOException,
            UnknownCodonException {
        StringBuilder genomeString = readGenomeFile(genomeFile);
        ProteinOutputterGenerator outputterGenerator = new ProteinOutputterGenerator(
                databaseName, genomeString, table);
        ProteinLocationFileGenerator.generateFile(locations, outputWriter,
                outputterGenerator);
    }

    private StringBuilder readGenomeFile(File genomeFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(genomeFile));
        StringBuilder sequence = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.matches("^>.*$")) {
                continue;
            }
            sequence.append(line);
        }
        reader.close();
        return sequence;
    }

}
