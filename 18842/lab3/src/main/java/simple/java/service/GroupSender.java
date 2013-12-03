package simple.java.service;

import lombok.extern.log4j.Log4j;
import simple.java.manager.GroupManager;
import simple.java.model.Group;
import simple.java.model.Message;
@Log4j
public class GroupSender {

	public static void send(Message msg){
		Group g = GroupManager.getGroupByName(msg.getGname());
		
		if(g == null){
			log.debug("Group does not exit");
			return;
		}
		
		for(String dest: g.nodeNames){
			msg.setDest(dest);
			Sender.send(msg);
		}
	}
}
