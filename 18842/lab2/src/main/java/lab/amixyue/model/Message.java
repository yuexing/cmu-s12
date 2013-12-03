package lab.amixyue.model;

import java.io.Serializable;

import lab.amixyue.constant.MessageType;
import lombok.Data;

public @Data class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int ID = -1;
	private static int GID = -1;
	
	protected String src;
	protected String dest;
	protected MessageType type;
	protected Object data;
	protected int id;
	protected int gid;
	protected int requestid;
	protected String gname = null;
	
	public Message(){
		
	}
	public Message(String src, String dest, MessageType type, Object data){
		this.src = src;
		this.dest = dest;
		this.type = type;
		this.data = data;
	} 
	
	public void setId(){
		this.id = ++ID;
	}
	
}
