package lab1.msg;

/**
 * A message used to reply an OK.
 * @author amixyue
 *
 */
@SuppressWarnings("serial")
public class OKMsg extends Message {
	/**
	 * 
	 */
	public OKMsg() {
		super(Message.MessageType.OK, null);
	}
}
