package lab.amixyue.model;

import java.util.Vector;

import lombok.*;

public @Data class VectorClockMessage extends Message {

	private Vector<Integer> vector;
	
	public VectorClockMessage(){
		vector = new Vector<Integer>();
	}
	public static VectorClockMessage msg2VCMsg(Message msg){
		VectorClockMessage tmp = new VectorClockMessage();
		tmp.src = msg.src;
		tmp.dest = msg.dest;
		tmp.data = msg.data;
		tmp.id = msg.id;
		return tmp;
	}
	
	public String toString(){
		return super.toString()+ " @time: " + vector;		
	}
}
