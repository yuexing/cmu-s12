package lab.amixyue.send;

public abstract class SendController {

	
	/**
	 * delayQueue first, sendQueue next
	 */
	public abstract boolean send(String ip, int port, Object data);
	
}
