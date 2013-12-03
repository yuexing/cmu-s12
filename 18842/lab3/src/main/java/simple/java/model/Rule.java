package simple.java.model;

import simple.java.model.Message.MessageType;

public class Rule {

	public final String src;
	public final String dest;
	public final MessageType type;
	public final Action action;
	public final Integer id;
	public final Integer nth;
	
	public Rule(String src, String dest, MessageType type, Action action, Integer id, Integer nth){
		this.src = src;
		this.dest = dest;
		this.type = type;
		this.action = action ;
		this.id = id;
		this.nth = nth;
	}
	
	public String configStr(){
		return "\n- Action : "+ this.action
		+ (this.src != null? "\n  Src : " + this.src :"")
		+ (this.dest != null? "\n  Dest : " + this.dest :"")
		+ (this.type != null? "\n  Kind : " + this.type :"")
		+ (this.id != null? "\n  ID : " + this.id :"")
		+ (this.nth != null? "\n  Nth : " + this.nth:"");
	}
	public enum Action{drop, duplicate, delay, none}
}
