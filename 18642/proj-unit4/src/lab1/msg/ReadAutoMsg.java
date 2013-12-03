package lab1.msg;

/**
 * A message used to read an automotive.
 * @author amixyue
 *
 */
@SuppressWarnings("serial")
public class ReadAutoMsg extends Message {

	public ReadAutoMsg(String name) {
		super(Message.MessageType.READ_AUTOMOTIVE, name);
	}
}
