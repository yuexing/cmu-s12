package lab1.exception;

/**
 * A wrapper for load error. This wrapper will keep our interface to our users
 * very clean.
 * @author amixyue
 *
 */
public class LoaderException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoaderException(String message, Throwable cause){
		super(message, cause);
	}
	
	public LoaderException(String message){
		super(message);
	}
}
