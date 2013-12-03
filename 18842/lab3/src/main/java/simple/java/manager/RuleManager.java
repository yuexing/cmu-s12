package simple.java.manager;

import java.util.ArrayList;

import simple.java.model.*;
import simple.java.model.Rule.Action;

public class RuleManager {

	private static ArrayList<Rule> sendRules;
	private static ArrayList<Rule> recvRules;
	
	static{
		sendRules = new ArrayList<Rule>();
		recvRules = new ArrayList<Rule>();
	}
	
	public static void addSendRule(Rule rule){
		sendRules.add(rule);
	}
	
	public static void addRecvRule(Rule rule){
		recvRules.add(rule);
	}
	
	public static void clearSendRule(){
		sendRules.clear();
	}
	
	public static void clearRecvRule(){
		recvRules.clear();
	}
	
	public static Action checkSend(Message msg){
		for (Rule rule: sendRules){
			if((rule.src==null||rule.src!=null && rule.src.equals(msg.src))&&
					(rule.dest==null||rule.dest!=null && rule.dest.equals(msg.getDest()))&&
					(rule.type==null||rule.type!=null && rule.type.equals(msg.type))&&
					(rule.id==null||rule.id!=null && rule.id == msg.getId())&&
					(rule.nth == null||rule.nth != null && rule.nth != 0 && (msg.getId()%rule.nth)==0)){
				//first match will return
				return rule.action;
			}
		}
		return Action.none;
	}
	
	public static Action checkRecv(Message msg){
		for (Rule rule: recvRules){
			if((rule.src==null||rule.src!=null && rule.src.equals(msg.src))&&
					(rule.dest==null||rule.dest!=null && rule.dest.equals(msg.getDest()))&&
					(rule.type==null||rule.type!=null && rule.type.equals(msg.type))&&
					(rule.id==null||rule.id!=null && rule.id == msg.getId())&&
					(rule.nth == null||rule.nth != null && rule.nth != 0 && (msg.getId()%rule.nth)==0)){
				//first match will return
				return rule.action;
			}
		}
		return Action.none;
	}
	
	public static int countSend(){
		return sendRules.size();
	}
	
	public static int countRecv(){
		return recvRules.size();
	}
	
	public static ArrayList<Rule> getSendRules(){
		return sendRules;
	}
	
	public static ArrayList<Rule> getRecvRules(){
		return recvRules;
	}
}
