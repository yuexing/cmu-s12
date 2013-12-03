package lab1.exception;

public class AutomotiveException extends Exception {

	private static final long serialVersionUID = 1L;

	public AutomotiveException(String message, Throwable cause){
		super(message, cause);
	}
	
	public AutomotiveException(String message){
		super(message);
	}
	
	public static class ErrMsg {

		public static final String NO_PERMIT = "You have no permit to do this operation."; 
		public static final String NO_SUCH = "No such automotive, please check!";
		public static final String USER_OP = "This is an user operation, please switch your role";
		public static final String NOT_SUP = "This is not support in current version";
	}

}
