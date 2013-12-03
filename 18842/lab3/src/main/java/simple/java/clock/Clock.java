package simple.java.clock;

import simple.java.model.Message;

public interface Clock {

	public enum ClockType{logic, vector};
	
	//set time to msg
	public void setTime(final Message msg);
	//update myself
	public void updateTime(final Message msg);
	//parse to timed message
	//settime can be called here
	public Message parse(Message msg);
}
