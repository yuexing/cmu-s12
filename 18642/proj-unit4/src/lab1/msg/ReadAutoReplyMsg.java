package lab1.msg;

import lab1.model.Automotive;

/**
 * A message used to reply to a request which is reading an automotive.
 * 
 * @author amixyue
 * 
 */
@SuppressWarnings("serial")
public class ReadAutoReplyMsg extends Message {

	public ReadAutoReplyMsg(Automotive data) {
		super(Message.MessageType.READ_AUTOMOTIVE_REPLY, data);
	}
}
