package lab.amixyue.pipeline.valve;

import java.util.Queue;

import lab.amixyue.constant.Global;
import lab.amixyue.constant.MessageType;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class RecvReleaseValve extends Valve {

	public RecvReleaseValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvReleaseValve ");
		Context context = session.getContext();
		Message msg = (Message) session.getAttribute(Global.prefix + "recvMsg");

		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}

		if (!msg.getType().equals(MessageType.release)) {
			return;
		}

		context.setAttribute("vote", false);
		@SuppressWarnings("unchecked")
		Queue<Message> requestQ = (Queue<Message>) context
				.getAttribute("!@#$%^&*requestq");
		if (requestQ == null) {
			return;
		}
		for (Message m : requestQ) {
			if (m.getSrc().equals(msg.getSrc())
					&& m.getRequestid() == msg.getRequestid()) {
				requestQ.remove(m);
			}
		}

		if (requestQ.size() > 0) {
			context.setAttribute("vote", true);
			// ack
			log.debug("ack sending...");
			msg = requestQ.peek();
			requestQ.remove();
			Message tmp = new Message(context.getMeNode().getName(),
					msg.getSrc(), MessageType.ack, null);
			msg.setRequestid(msg.getRequestid());
			session.setAttribute("!@#$%^&*sendMsg", tmp);
			//get away from presend
			session.setAttribute("!@#$%^&*recvMsg", null);
			subPipe().process(session);
		}
	}

}
