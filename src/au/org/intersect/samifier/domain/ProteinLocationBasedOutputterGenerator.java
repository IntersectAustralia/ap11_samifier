package au.org.intersect.samifier.domain;

public interface ProteinLocationBasedOutputterGenerator {
    Outputter getOutputterFor(ProteinLocation proteinLocation);
}
