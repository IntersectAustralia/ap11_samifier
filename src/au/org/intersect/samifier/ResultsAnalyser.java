package au.org.intersect.samifier;

import java.io.File;

import au.org.intersect.samifier.domain.DebuggingFlag;
import au.org.intersect.samifier.reporter.ReportLister;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import au.org.intersect.samifier.reporter.DatabaseHelper;
import au.org.intersect.samifier.runner.ResultAnalyserRunner;

public class ResultsAnalyser implements Version {
    public static void main(String... args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("-v")) {
            System.out.println("Version = " + VERSION);
            System.exit(0);
        }
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Mascot search results file in txt format");
        OptionBuilder.withArgName("searchResultsFile");
        OptionBuilder.isRequired();
        Option resultsFile = OptionBuilder.create("r");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("File mapping protein identifier to ordered locus name");
        OptionBuilder.withArgName("mappingFile");
        OptionBuilder.isRequired();
        Option mappingFile = OptionBuilder.create("m");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Genome file in gff format");
        OptionBuilder.withArgName("genomeFile");
        OptionBuilder.isRequired();
        Option genomeFileOpt = OptionBuilder.create("g");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Filename to write the SAM format file to");
        OptionBuilder.withArgName("outputFile");
        OptionBuilder.isRequired();
        Option outputFile = OptionBuilder.create("o");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Directory containing the chromosome files in FASTA format for the given genome");
        OptionBuilder.withArgName("chromosomeDir");
        OptionBuilder.isRequired();
        Option chrDirOpt = OptionBuilder.create("c");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("sqlQuery");
        OptionBuilder
                .withDescription("Filters the result through the use of a SQL statement to the output file");
        Option sqlOpt = OptionBuilder.create("sql");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("reportList");
        OptionBuilder
                .withDescription("A file containing all the pre-built SQL queries");
        Option repListOpt = OptionBuilder.create("replist");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("reportId");
        OptionBuilder.withDescription("Access a built in report query");
        Option repIdOpt = OptionBuilder.create("rep");

        Options options = new Options();

        if (DebuggingFlag.get_sbi_debug_flag() == 1) {
            OptionBuilder.hasArg();
            OptionBuilder
                    .withDescription("File containing a mapping of codons to amino acids, in the format used by NCBI.");
            OptionBuilder.withArgName("Translation Table File");
            OptionBuilder.isRequired();
            Option translationTableOpt = OptionBuilder.create("t");
            options.addOption(translationTableOpt);
        }

        options.addOption(resultsFile);
        options.addOption(mappingFile);
        options.addOption(genomeFileOpt);
        options.addOption(outputFile);
        options.addOption(chrDirOpt);
        options.addOption(sqlOpt);
        options.addOption(repListOpt);
        options.addOption(repIdOpt);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            String[] searchResultsPaths = line.getOptionValues("r");
            File genomeFile = new File(line.getOptionValue("g"));
            File mapFile = new File(line.getOptionValue("m"));
            File outfile = new File(line.getOptionValue("o"));
            File chromosomeDir = new File(line.getOptionValue("c"));
            String sqlQuery = line.getOptionValue("sql");
            String repListFile = line.getOptionValue("replist");
            String repId = line.getOptionValue("rep");

            // / Change by Ignatius Pang *%*%*%
            // / This debug flag is currently set to provide internal validation
            // alternatively spliced peptides.
            // / The nucleotide sequence in the 'output' SAM file is compared
            // with the amino acid sequence in the 'input' Mascot DAT or
            // mzIdentML file.
            if (DebuggingFlag.get_sbi_debug_flag() == 1) {
                File translationTableFile = new File(line.getOptionValue("t"));
                ResultAnalyserRunner analyser = new ResultAnalyserRunner(
                        searchResultsPaths, genomeFile, mapFile, outfile,
                        chromosomeDir, translationTableFile);
                if (sqlQuery == null && repId == null) {
                    analyser.run();
                } else if (sqlQuery != null
                        && (repId != null || repListFile != null)) {
                    System.err.println("Only use either reportId or sqlQuery.");
                } else if (sqlQuery != null) {
                    mainWithQuery(analyser, sqlQuery);
                } else {
                    mainWithReportId(analyser, repId, repListFile);
                }
            } else {
                ResultAnalyserRunner analyser = new ResultAnalyserRunner(
                        searchResultsPaths, genomeFile, mapFile, outfile,
                        chromosomeDir);
                if (sqlQuery == null && repId == null) {
                    analyser.run();
                } else if (sqlQuery != null
                        && (repId != null || repListFile != null)) {
                    System.err.println("Only use either reportId or sqlQuery.");
                } else if (sqlQuery != null) {
                    mainWithQuery(analyser, sqlQuery);
                } else {
                    mainWithReportId(analyser, repId, repListFile);
                }
            }

        } catch (ParseException pe) {
            System.err.println("Version = " + VERSION);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("result_analyser", options, true);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Version = " + VERSION);
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void mainWithQuery(ResultAnalyserRunner analyser,
            String sqlQuery) throws Exception {
        if (sqlQuery.isEmpty()) {
            DatabaseHelper db = new DatabaseHelper();
            db.printTableDetails(null);
            db.shutdown();
            return;
        }
        analyser.initMemoryDb();
        analyser.runWithQuery(sqlQuery);
    }

    private static void mainWithReportId(ResultAnalyserRunner analyser,
            String repId, String repListFile) throws Exception {
        if (repListFile == null || !(new File(repListFile)).exists()) {
            System.err.println("Cannot use reportId if no reportList is used");
            return;
        }
        String sqlQuery = getQueryFromFile(repId, repListFile);
        if (sqlQuery == null || sqlQuery.isEmpty()) {
            System.err.println("reportId does not exists or query is empty");
            return;
        }
        analyser.initMemoryDb();
        analyser.runWithQuery(sqlQuery);
    }

    private static String getQueryFromFile(String repId, String repListFile) {
        ReportLister reportLister = new ReportLister(repListFile);
        return reportLister.getQueryByReportId(repId);
    }
}
