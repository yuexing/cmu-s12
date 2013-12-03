package lab.amixyue.model;

import java.io.Serializable;

import lab.amixyue.constant.Protocal;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String ip;
	private Integer port;
	private Protocal protocal;
	
	/**
	 * construct a yaml string for the Node object 
	 * @return yaml string
	 */
	public String configStr(){
		return "\n- Name : " + this.name
		+ "\n  IP : " + this.ip
		+ "\n  Port : " + this.port
		+ "\n  Protocol : " + this.protocal;
	}
	/**
	 * deep copy
	 */
	public void copyOf(Node n){
		this.name = n.name;
		this.ip = n.ip;
		this.port = n.port;
		this.protocal = n.protocal;
	}
}
