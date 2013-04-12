package au.org.intersect.samifier.runner;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.intersect.samifier.domain.AccessionOutputterGenerator;
import au.org.intersect.samifier.domain.CodonTranslationTable;
import au.org.intersect.samifier.domain.GffOutputterGenerator;
import au.org.intersect.samifier.domain.ProteinLocation;
import au.org.intersect.samifier.domain.ProteinOutputterGenerator;
import au.org.intersect.samifier.domain.UnknownCodonException;
import au.org.intersect.samifier.generator.CodonsPerIntervalLocationGenerator;
import au.org.intersect.samifier.generator.GlimmerFileLocationGenerator;
import au.org.intersect.samifier.generator.LocationGenerator;
import au.org.intersect.samifier.parser.FastaParser;
import au.org.intersect.samifier.parser.FastaParserException;
import au.org.intersect.samifier.parser.FastaParserImpl;
import au.org.intersect.samifier.util.ProteinLocationFileGenerator;

public class ProteinGeneratorRunner {

    private String glimmerFilePath;
    private FastaParser fastaParser;
    private File genomeFile;
    private String interval;
    private String databaseName;
    private Writer outputWriter;
    private File translationTableFile;
    private Writer gffWriter;
    private Writer accessionWriter;

    public ProteinGeneratorRunner(String glimmerFilePath, File genomeFile,
            String interval, String databaseName, Writer outputWriter,
            File translationTableFile, Writer gffWriter, Writer accessionWriter) throws FastaParserException{
        this.glimmerFilePath = glimmerFilePath;
        this.genomeFile = genomeFile;
        this.interval = interval;
        this.databaseName = databaseName;
        this.outputWriter = outputWriter;
        this.translationTableFile = translationTableFile;
        this.gffWriter = gffWriter;
        this.accessionWriter = accessionWriter;
        this.fastaParser = new FastaParserImpl(genomeFile);
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
                    interval, fastaParser);
        }
        return locationGenerator;
    }

    private void generateGffFile(List<ProteinLocation> locations)
            throws IOException {
        String genomeFileName = genomeFile.getName();
        GffOutputterGenerator outputterGenerator = new GffOutputterGenerator(genomeFileName);
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
            UnknownCodonException, FastaParserException {
        //check for contig
        //divide by chromosome
        Map<String, List<ProteinLocation>> locationByChromosome = new HashMap<String, List<ProteinLocation>>();
        for (ProteinLocation location : locations) {
            String chromosome = location.getChromosome();
            List<ProteinLocation> list = locationByChromosome.get(chromosome);
            if (list == null) {
                list = new ArrayList<ProteinLocation>();
            }
            list .add(location);
            locationByChromosome.put(chromosome, list);
        }
        if (locationByChromosome.keySet().size() > 1) {
            for (String chromosome : locationByChromosome.keySet()) {
                for (ProteinLocation location : locationByChromosome.get(chromosome)) {
                    location.setName(chromosome + "_" + location.getName());
                }
            }
        }
        for (String chromosome : locationByChromosome.keySet()) {
            StringBuilder genomeString = readGenomeFile(chromosome);
            List<ProteinLocation> locationForChromosome = locationByChromosome.get(chromosome);
            ProteinOutputterGenerator outputterGenerator = new ProteinOutputterGenerator(
                databaseName, genomeString, table);
            ProteinLocationFileGenerator.generateFile(locationForChromosome, outputWriter,
                outputterGenerator);
        }
        if (outputWriter != null) {
            outputWriter.close();
        }
    }

    private StringBuilder readGenomeFile(String chromosome) throws IOException, FastaParserException{
        return new StringBuilder(fastaParser.readCode(chromosome));
    }


}
