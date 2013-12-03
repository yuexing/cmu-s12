package simple.java.clock;

import simple.java.model.LogicTimedMessage;
import simple.java.model.Message;
/**
 * whether it should be static needs consideration
 * to make it easy, just use object
 * @author amy
 *
 */
public class LogicClock implements Clock {

	/**
	 * expected time here
	 */
	private long time;
	
	public void setTime(Message msg) {
		
	}

	/**
	 * only increase
	 */
	public void updateTime(Message msg) {
		if (msg == null)
			return;
		LogicTimedMessage tmp = (LogicTimedMessage)msg;
		if(tmp.getTime() > time){
			time = tmp.getTime();
		}
		time++;
	}

	/**
	 * generate a timed message
	 */
	public Message parse(Message msg) {
		LogicTimedMessage tmp = new LogicTimedMessage(msg.src, msg.getDest(), msg.type, msg.data, ++time);
		tmp.setGname(msg.getGname());
		tmp.setAck(msg.isAck());
		tmp.setDeliverNo(msg.getDeliverNo());
		tmp.setRequestNo(msg.getRequestNo());
		tmp.setSendNo(msg.getSendNo());
		//log.debug(tmp.src + ", " + tmp.dest + ", " + tmp.type + ", " + tmp.data + ", " + time);
		return tmp;
	}

}
