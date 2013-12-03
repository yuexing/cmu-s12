package lab.amixyue.model;

import lombok.*;


public @Data class LogicTimeMessage extends Message {
	
	private int time;
	
	public static LogicTimeMessage msg2LTMsg(Message msg){
		LogicTimeMessage tmp = new LogicTimeMessage();
		tmp.id = msg.id;
		tmp.src = msg.src;
		tmp.dest = msg.dest;
		tmp.type = msg.type;
		//deep copy
		tmp.data = msg.data;
		return tmp;
	}
	
	public String toString(){
		return super.toString()+ " @time: " + time;		
	}
}
