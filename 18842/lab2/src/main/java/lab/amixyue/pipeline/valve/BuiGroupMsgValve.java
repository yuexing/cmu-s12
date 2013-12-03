package lab.amixyue.pipeline.valve;

import java.util.*;

import lab.amixyue.constant.GStatus;
import lab.amixyue.constant.Global;
import lab.amixyue.context.*;
import lab.amixyue.model.Group;
import lab.amixyue.model.Message;
import lab.amixyue.model.Node;
import lab.amixyue.pipeline.Pipeline;
import lab.amixyue.util.NodeUtil;
import lombok.extern.log4j.Log4j;

/**
 * input : msg{dest:gname, type:group, data:nnames}
 * output: msg{dest:broad, type:group, data:group}
 * @author amy
 *
 */
@Log4j
public class BuiGroupMsgValve extends Valve {

	public BuiGroupMsgValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter BuiGroupMsgValve ");
		//group msg
		Message msg = (Message)session.getAttribute(Global.prefix+"sendMsg");
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}
		//String[] to Node[]
		String[] nNames = (String[]) msg.getData();
		ArrayList<Node> nodes = session.getContext().getNodes();
		ArrayList<Node> mems = new ArrayList<Node>();
		Node tmpNode;
		
		for(String nName : nNames){
			tmpNode = NodeUtil.getNodeByName(nodes, nName);
			if(tmpNode != null)
				mems.add(tmpNode);
		}		
		
		//make a group
		Group g = new Group(msg.getDest(), mems, GStatus.idle);	
			
		//update msg
		session.setAttribute(Global.prefix+"groupcast", true);
		msg.setData(g);
		msg.setDest(Global.prefix+"broad");
		session.setAttribute(Global.prefix+"sendMsg", msg);
	}

}
