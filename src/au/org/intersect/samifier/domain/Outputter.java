package au.org.intersect.samifier.domain;

public interface Outputter {
    String getOutput() throws OutputException;
}
