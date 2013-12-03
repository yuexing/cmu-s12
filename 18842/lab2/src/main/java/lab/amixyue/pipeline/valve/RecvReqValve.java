package lab.amixyue.pipeline.valve;

import java.util.LinkedList;
import java.util.Queue;

import lab.amixyue.constant.Global;
import lab.amixyue.constant.MessageType;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class RecvReqValve extends Valve {

	public RecvReqValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter ResvReqValve ");
		Context context = session.getContext();
		Message msg = (Message) session.getAttribute(Global.prefix + "recvMsg");

		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}

		if(!msg.getType().equals(MessageType.request)){
			return;
		}
		
		Boolean vote = (Boolean) context.getAttribute("vote");

		if (vote) {
			@SuppressWarnings("unchecked")
			Queue<Message> requestQ = (Queue<Message>) context
					.getAttribute("!@#$%^&*requestq");
			if (requestQ == null) {
				requestQ = new LinkedList<Message>();
				context.setAttribute("!@#$%^&*requestq", requestQ);
			}
			requestQ.add(msg);
		} else {
			context.setAttribute("vote", true);
			
			// ack
			log.debug("ack sending...");
			Message tmp = new Message(context.getMeNode().getName(), msg.getSrc(),
					MessageType.ack, null);
			msg.setRequestid(msg.getRequestid());
			session.setAttribute("!@#$%^&*sendMsg", tmp);
			session.setAttribute("!@#$%^&*recvMsg", null);
			subPipe().process(session);
			
			//session.setAttribute("!@#$%^&*sendMsg", msg);
		}

	}

}
