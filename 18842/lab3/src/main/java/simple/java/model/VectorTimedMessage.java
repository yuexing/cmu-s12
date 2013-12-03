package simple.java.model;

import java.util.Vector;

public class VectorTimedMessage extends Message implements Comparable<VectorTimedMessage>{

	private Vector<Long> vector;
	
	public VectorTimedMessage(String src, String dest, MessageType type,
			Object data) {
		super(src, dest, type, data);
	}
	
	public VectorTimedMessage(String src, String dest, MessageType type,
			Object data, Vector<Long> vector) {
		super(src, dest, type, data);
		this.vector = new Vector<Long>(vector);
	}
	
	public void setVector(Vector<Long> vector){
		this.vector = new Vector<Long>(vector);
	}
	
	public Vector<Long> getVector(){
		return this.vector;
	}

	public String toString(){
		if(this.gname != null){
			return "@" + this.vector.toString() +": " + this.src + ", " + this.dest
			+ " in " + this.gname + ", sendNo " + this.sendNo
			+ ", latest Deliverd " + this.deliverNo
			+ ", " + this.type + ", " + this.data;
		}
		return "@" + this.vector.toString() +": " + this.src + ", " + this.dest +", " + this.type + ", " + this.data;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int compareTo(VectorTimedMessage msg) {
		int i = 0;
		int sign = 0;
		while(i < this.vector.size()){
			if(this.vector.get(i) < msg.vector.get(i) && sign != 1){
				sign  = -1;
			}else if(this.vector.get(i) > msg.vector.get(i) && sign != -1){
				sign = 1;
			}else if(this.vector.get(i) == msg.vector.get(i)){
				//no contribution
			}else{
				sign = 0;
			}
		}
		return sign;
	}

}
