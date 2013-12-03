package lab1.msg;

import java.io.Serializable;

/**
 * This models the message sent btw client and server.
 * 
 * @author amixyue
 * 
 */
@SuppressWarnings("serial")
public class Message implements Serializable{

	public static enum MessageType {
		ADD_AUTOMOTIVE, READ_AUTOMOTIVE, OK, READ_AUTOMOTIVE_REPLY
	}

	protected MessageType type;
	protected Object data;

	/**
	 * @param type
	 * @param data
	 */
	protected Message(MessageType type, Object data) {
		super();
		this.type = type;
		this.data = data;
	}
	
	/**
	 * @return the type
	 */
	public MessageType getType() {
		return type;
	}	

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(MessageType type) {
		this.type = type;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
