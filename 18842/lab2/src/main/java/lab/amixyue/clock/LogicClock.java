package lab.amixyue.clock;

import lab.amixyue.constant.ClockType;
import lab.amixyue.context.Context;
import lab.amixyue.model.LogicTimeMessage;
import lab.amixyue.model.Message;
import lombok.extern.log4j.Log4j;
@Log4j
public class LogicClock implements ClockService {

	private int time;
	
	public static ClockServiceFactory factory = new ClockServiceFactory() {
		
		public ClockService getClock(Context context) {
			return new LogicClock();
		}
	};
	private LogicClock(){
		time = 0;
	}
	
	public synchronized void update(Message msg) {
		time++;
		if(time < ((LogicTimeMessage)msg).getTime())
			time = ((LogicTimeMessage)msg).getTime();
	}

	public synchronized Message setTime(Message msg) {
		time++;
		LogicTimeMessage tmp =LogicTimeMessage.msg2LTMsg(msg);
		tmp.setTime(time);
		log.debug("logic timestamped: " + tmp);
		return tmp;
	}
	
	public synchronized void update() {
		time++;		
	}

	public ClockType getType() {
		return ClockType.logic;
	}		


}
