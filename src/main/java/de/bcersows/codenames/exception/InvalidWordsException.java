package de.bcersows.codenames.exception;

/**
 * If the word loading failed.
 * 
 * @author bcersows
 */
public class InvalidWordsException extends CodenameException {

    private static final long serialVersionUID = -3817466686814782343L;

    /**
     * @param message
     * @param cause
     */
    public InvalidWordsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public InvalidWordsException(final String message) {
        super(message);
    }
}
