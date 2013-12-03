package lab.amixyue.pipeline.valve;

import java.util.ArrayList;
import java.util.Map;

import lab.amixyue.constant.Global;
import lab.amixyue.constant.MessageType;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Group;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class RecvAckValve extends Valve {

	public RecvAckValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvAckValve ");
		Context context = session.getContext();
		Message msg = (Message) session.getAttribute(Global.prefix + "recvMsg");

		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}

		if (!msg.getType().equals(MessageType.ack)) {
			return;
		}

		// group/node : rid
		@SuppressWarnings("unchecked")
		Map<Integer, Group> rid_gs = (Map<Integer, Group>) context
				.getAttribute(Global.prefix + "rid_gs");

		// rid : act(strategy)
		@SuppressWarnings("unchecked")
		Map<Integer, Object> ridacts = (Map<Integer, Object>) context
				.getAttribute(Global.prefix + "ridacts");

		// rid : acks
		@SuppressWarnings("unchecked")
		Map<Integer, ArrayList<Message>> ridacks = (Map<Integer, ArrayList<Message>>) context
				.getAttribute(Global.prefix + "ridacks");
		
		if(ridacks == null || ridacts == null || rid_gs == null){
			//can be broken
			return;
		}
		int rid = msg.getRequestid();
		ridacks.get(rid).add(msg);
		
		if(ridacks.get(rid).size()==rid_gs.get(rid).getNodes().size()-1){
			//enter CS
			log.debug("enter CS: " + ridacts.get(rid));
			String dest = rid_gs.get(rid).getName();
			Message tmp = new Message(context.getMeNode().getName(), 
					dest, MessageType.release, null);
			tmp.setRequestid(rid);
			tmp.setGname(dest);
		}else{
			//wait
		}
	}
}
