package simple.java.manager;

import java.util.*;

import lombok.extern.log4j.Log4j;

import simple.java.model.*;
@Log4j
public class GroupManager {

	private static HashMap<String, Group> groups;
	
	static{
		groups = new HashMap<String, Group>();
	}
	
	public static Group getGroupByName(String name){
		Group group = null;
		if(groups.containsKey(name))
			group = groups.get(name);
		return group;
	}

	public static void addGroup(Group group) {
		groups.put(group.name, group);
	}

	public static void clear() {
		groups.clear();
	}
	
	public static ArrayList<Message> getAvailableMsgs(){
		ArrayList<Message> msgs = new ArrayList<Message>();
		for(Group group : groups.values()){
			msgs.addAll(group.getAvailableMsgFromHoldback());
		}
		Collections.sort(msgs, new Comparator<Message>() {

			public int compare(Message msg1, Message msg2) {
				if(msg1 instanceof LogicTimedMessage){
					return ((LogicTimedMessage)msg1).compareTo((LogicTimedMessage)msg2);
				}else if(msg1 instanceof VectorTimedMessage){
					return ((VectorTimedMessage)msg1).compareTo((VectorTimedMessage)msg2);
				}else{
					log.debug("Compare Error due to cast error");
					return 0;
				}				
			}
		});
		return msgs;
	}

	public static HashMap<String, Group> getGroups() {
		return groups;
	}

	public static void setGroups(HashMap<String, Group> groups) {
		GroupManager.groups = groups;
	}
}