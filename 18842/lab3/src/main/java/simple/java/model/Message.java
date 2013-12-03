package simple.java.model;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int ID = 0;
	public static enum MessageType{request, ack, ask, test, release, gack, reack, gask}
	
	public final String src;

	protected String dest;
	public final MessageType type;
	public final Object data;
	private int id;
	
	/**
	 * for group message
	 */

	protected String gname;
	protected int sendNo;
	protected boolean ack;
	protected int deliverNo;
	protected int requestNo;
	
	public Message(String src, String dest, MessageType type, Object data){
		this.src = src;
		this.dest = dest;
		this.type = type;
		this.data = data;
	}
	
	public void setId(){
		this.id = ID++;
	}
	
	public int getId(){
		return this.id;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public int getSendNo() {
		return sendNo;
	}

	public void setSendNo(int sendNo) {
		this.sendNo = sendNo;
	}

	public boolean isAck() {
		return ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}

	public int getDeliverNo() {
		return deliverNo;
	}

	public void setDeliverNo(int deliverNo) {
		this.deliverNo = deliverNo;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public int getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(int requestNo) {
		this.requestNo = requestNo;
	}


}
