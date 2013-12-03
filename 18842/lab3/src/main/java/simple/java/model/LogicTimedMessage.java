package simple.java.model;

public class LogicTimedMessage extends Message implements Comparable<LogicTimedMessage>{

	private long time;
	
	public LogicTimedMessage(String src, String dest, MessageType type,
			Object data) {
		super(src, dest, type, data);
	}
	
	public LogicTimedMessage(String src, String dest, MessageType type,
			Object data, long time) {
		super(src, dest, type, data);
		this.time = time;
	}

	public void setTime(long time){
		this.time = time;
	}
	
	public long getTime(){
		return this.time;
	}
	
	public String toString(){
		if(this.gname != null){
			return "@" + this.time + ": "+ this.src + ", " + this.dest
			+ " in " + this.gname + ", sendNo " + this.sendNo
			+ ", latest Deliverd " + this.deliverNo
			+ ", " + this.type + ", " + this.data;
		}
		return "@" + this.time + ": "+ this.src + ", " + this.dest +", " + this.type + ", " + this.data;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int compareTo(LogicTimedMessage msg) {
		if(this.time < msg.time){
			return -1;
		}else if(this.time > msg.time){
			return 1;
		}
		return 0;
	}

}
