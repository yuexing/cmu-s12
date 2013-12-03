package beauty.web.exception;

public class ParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ParserException(Throwable arg1) {
		super(arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 */
	public ParserException(String arg0) {
		super(arg0);
	} 
}
