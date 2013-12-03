package lab1.exception;

/**
 * An exception wrapper for socket errors.
 * @author amixyue
 *
 */
public class SocketException extends Exception {

	private static final long serialVersionUID = 1L;

	public SocketException(String message, Throwable cause){
		super(message, cause);
	}
	
	public SocketException(String message){
		super(message);
	}
}
