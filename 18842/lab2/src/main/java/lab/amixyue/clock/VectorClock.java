package lab.amixyue.clock;

import java.util.Vector;

import lab.amixyue.constant.ClockType;
import lab.amixyue.context.Context;
import lab.amixyue.model.Message;
import lab.amixyue.model.VectorClockMessage;
import lombok.extern.log4j.Log4j;
@Log4j
public class VectorClock implements ClockService {

	Vector<Integer> vector;
	int n;
	int meIndex;
	
	public static ClockServiceFactory factory = new ClockServiceFactory() {
		
		public ClockService getClock(Context context) {
			int n = context.getNodes().size();
			int meIndex = context.getMeIndex();
			return new VectorClock(n, meIndex);
		}
	};
	private VectorClock(int n, int meIndex){
		this.n = n;
		this.meIndex = meIndex;
		vector = new Vector<Integer>(n);
	}
	
	public synchronized void update(Message msg) {
		int meTime =vector.get(meIndex);
		vector.set(meIndex, meTime++);
		
		VectorClockMessage tmp = (VectorClockMessage)msg;
		for(int i =0; i <n; i++){
			if(vector.get(i)< tmp.getVector().get(i))
				vector.set(i, tmp.getVector().get(i));
		}
	}

	public synchronized Message setTime(Message msg) {
		int meTime =vector.get(meIndex);
		vector.set(meIndex, meTime++);
		
		VectorClockMessage tmp = VectorClockMessage.msg2VCMsg(msg);
		tmp.setVector(vector);
		log.debug("vector timestamp: "+tmp);
		return tmp;
	}

	public synchronized void update() {
		int meTime =vector.get(meIndex);
		vector.set(meIndex, meTime++);
	}

	public ClockType getType() {
		return ClockType.vector;
	}
}
