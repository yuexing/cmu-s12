package lab.amixyue.model;

import lab.amixyue.constant.Action;
import lab.amixyue.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class Rule {
	private String src;
	private String dest;
	private MessageType type;
	private Action action;
	private Integer id;
	private Integer nth;
	
	/**
	 * construct a yaml string for the Node object 
	 * @return yaml string
	 */
	public String configStr(){
		return "\n- Action : "+ this.action
		+ (this.src != null? "\n  Src : " + this.src :"")
		+ (this.dest != null? "\n  Dest : " + this.dest :"")
		+ (this.type != null? "\n  Kind : " + this.type :"")
		+ (this.id != null? "\n  ID : " + this.id :"")
		+ (this.nth != null? "\n  Nth : " + this.nth:"");
	}
	
	
	public Action check(Message msg){
		if((this.src == null || (this.src!=null&&this.src.equals(msg.getSrc())))
				&&(this.dest == null || (this.dest!=null&&this.dest.equals(msg.getDest())))
				&&(this.type == null || (this.type!=null&&this.type.equals(msg.getType())))
				&&(this.id == null || (this.id!=null&&this.id.equals(msg.getId())))
				&&(this.nth == null || (this.nth!=null&&this.nth!=0&&msg.getId()%this.nth==0))){
			return this.action;
		}
		return Action.none;
	}
}
