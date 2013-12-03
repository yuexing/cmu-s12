package lab.amixyue.clock;

import lab.amixyue.constant.ClockType;
import lab.amixyue.model.Message;

public interface ClockService {

	public void update(Message msg);
	public void update();
	public Message setTime(Message msg);
	public ClockType getType();
}
