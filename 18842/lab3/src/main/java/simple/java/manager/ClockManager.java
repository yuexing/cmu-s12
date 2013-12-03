package simple.java.manager;

import simple.java.clock.Clock;
import simple.java.clock.LogicClock;
import simple.java.clock.VectorClock;
import simple.java.clock.Clock.ClockType;

public class ClockManager {

	public static ClockType type;
	public static Clock clock;
	
	/**
	 * init also means restart which should be controlled outside there
	 * @param type
	 */
	public static void init(ClockType type){
		ClockManager.type = type;
		switch(type){
		case logic:
			clock = new LogicClock();
			break;
		case vector:
			//mock
			clock = new VectorClock(NodeManager.count());
			break;
		}
	}
}
