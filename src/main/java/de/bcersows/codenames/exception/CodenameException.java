package de.bcersows.codenames.exception;

/**
 * Generic exception.
 * 
 * @author bcersows
 */
public abstract class CodenameException extends Exception {
    private static final long serialVersionUID = -2968280238596767186L;

    /**
     * @param message
     * @param cause
     */
    public CodenameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public CodenameException(final String message) {
        super(message);
    }
}
