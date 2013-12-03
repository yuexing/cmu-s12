package lab.amixyue.pipeline.valve;


import java.util.ArrayList;
import java.util.HashMap;
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
public class SendReqValve extends Valve {

	public SendReqValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendReqValve ");
		Context context = session.getContext();
		Message msg = (Message)session.getAttribute("!@#$%^&*sendMsg");
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}
		
		if(!msg.getType().equals(MessageType.request)){
			return;	
		}
		
		
		// group/node : rid
		@SuppressWarnings("unchecked")
		Map<Integer, Group> rid_gs = (Map<Integer, Group>) context
		.getAttribute(Global.prefix+"rid_gs");
		
		// rid : act(strategy)
		@SuppressWarnings("unchecked")
		Map<Integer, Object> ridacts = (Map<Integer, Object>) context
				.getAttribute(Global.prefix+"ridacts");
		
		// rid : acks
		@SuppressWarnings("unchecked")
		Map<Integer, ArrayList<Message>> ridacks = (Map<Integer, ArrayList<Message>>) context
				.getAttribute(Global.prefix +"ridacks");
		//rid
		Integer rid = (Integer) context.getAttribute(Global.prefix+ "rid");
		if(rid == null){
			rid = 0;
			context.setAttribute(Global.prefix+"rid", rid);
		}
		if(ridacks == null){
			ridacks = new HashMap<Integer, ArrayList<Message>>();
			context.setAttribute(Global.prefix+"ridacks", ridacks);
		}
		if(rid_gs == null){
			rid_gs = new HashMap<Integer, Group>();
			context.setAttribute(Global.prefix+"rid_gs", rid_gs);
		}
		if(ridacts == null){
			ridacts = new HashMap<Integer, Object>();
			context.setAttribute(Global.prefix+"ridacts", ridacts);
		}
		//update msg
		msg.setRequestid(rid);
		//update context
		rid_gs.put(rid, ((Group)context.getAttribute(msg.getDest())));
		ridacts.put(rid, msg.getData());
		ridacks.put(rid, new ArrayList<Message>());
		context.setAttribute(Global.prefix+"rid", rid++);	
		
	}

}
