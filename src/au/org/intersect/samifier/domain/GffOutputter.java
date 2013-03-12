package au.org.intersect.samifier.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GffOutputter implements Outputter {
    private String genomeFileName;
    private Integer start;
    private Integer end;
    private String glimmerScore;
    private String directionFlag;
    private String glimmerName;
    private Set<String> virtualProteinNames;

    public GffOutputter(ProteinLocation location,
            String genomeFileNameWithExtension) {
        this.genomeFileName = genomeFileNameNoExtension(genomeFileNameWithExtension);
        this.start = location.getStartIndex();
        this.end = location.getStop();
        if (location.getConfidenceScore() != null) {
            this.glimmerScore = location.getConfidenceScore().toString();
        } else {
            this.glimmerScore = "0";
        }
        this.directionFlag = location.getDirection();
        this.glimmerName = location.getName();
        this.virtualProteinNames = location.getVirtualProteinNames();
    }

    @Override
    public String getOutput() {
        StringBuilder output = new StringBuilder();

        output.append(getOutputLine("gene", false));
        output.append(System.getProperty("line.separator"));

        output.append(getOutputLine("CDS", true));
        output.append(System.getProperty("line.separator"));

        return output.toString();

    }

    private String getOutputLine(String type, boolean addParent) {
        StringBuilder output = new StringBuilder();
        output.append(genomeFileName);
        column(output, "Glimmer");
        column(output, type);
        column(output, Integer.toString(start));
        column(output, Integer.toString(end));
        column(output, glimmerScore);
        column(output, directionFlag);
        //column(output, frame);
        column(output, "0"); // this is phase - we  currently don't use that
        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("Name=" + glimmerName);
        if (addParent) {
            attributes.add("Parent=" + glimmerName);
        } else {
            attributes.add("ID=" + glimmerName);
        }
        if (virtualProteinNames.size() > 0) {
            String virtualProteins = "";
            for (String s:virtualProteinNames) {
                virtualProteins += (virtualProteins == "" ? "" : ",") + s;
            }
            attributes.add("Virtual_protein=" + virtualProteins);
        }
        column(output, attributes);
        return output.toString();
    }

    private void column(StringBuilder buff, String field) {
        buff.append('\t');
        buff.append(field);
    }

    private void column(StringBuilder buff, List<String> attributes) {
        buff.append('\t');
        for (String attribute : attributes) {
            buff.append(attribute + ";");
        }
    }

    private String genomeFileNameNoExtension(String genomeFileName) {
        int indexOfDot = genomeFileName.lastIndexOf('.');
        if (indexOfDot > 0) {
            return genomeFileName.substring(0, indexOfDot);
        }
        return genomeFileName;
    }
}
