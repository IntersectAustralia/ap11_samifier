package au.org.intersect.samifier.generator;

/**
 * Exception thrown when error generating protein locations
 */
public class LocationGeneratorException extends Exception {
    /**

     */
    private static final long serialVersionUID = 6956907805606872283L;

    public LocationGeneratorException(String message, Exception e) {
        super(message, e);
    }

    public LocationGeneratorException(String message) {
        super(message);
    }

}
