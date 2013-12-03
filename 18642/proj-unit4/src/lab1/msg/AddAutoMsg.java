package lab1.msg;

import lab1.model.*;

/**
 * A message used to add an automotive.
 * @author amixyue
 *
 */
@SuppressWarnings("serial")
public class AddAutoMsg extends Message {

	/**
	 * 
	 */
	public AddAutoMsg(Automotive data) {
		super(Message.MessageType.ADD_AUTOMOTIVE, data);
	}
}
