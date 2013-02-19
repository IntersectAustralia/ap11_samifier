package au.org.intersect.samifier.domain;

public class AccessionOutputter implements Outputter {
    private String name;

    public AccessionOutputter(ProteinLocation proteinLocation) {
        this.name = proteinLocation.getName();
    }

    @Override
    public String getOutput() throws OutputException {
        return name + " " + name + " " + name
                + System.getProperty("line.separator");
    }
}
