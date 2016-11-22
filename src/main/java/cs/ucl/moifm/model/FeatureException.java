package cs.ucl.moifm.model;

/**
 * Exception to throw when you get an unexpected error in the MMF models. This
 * is only used for errors that should be presented to the user.
 */
public class FeatureException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public FeatureException() {
    }

    /**
     * @param message
     */
    public FeatureException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public FeatureException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public FeatureException(String message, Throwable cause) {
        super(message, cause);
    }

}
