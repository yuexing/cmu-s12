package lab1.exception;

/**
 * A wrapper for option not found error. This wrapper will keep our interface to
 * our users very clean.
 * 
 * @author amixyue
 * 
 */
public class OptionException extends Exception {

	private static final long serialVersionUID = 1L;

	public OptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public OptionException(String message) {
		super(message);
	}
}
