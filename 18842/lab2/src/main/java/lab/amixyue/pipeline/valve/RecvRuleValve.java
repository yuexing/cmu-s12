package lab.amixyue.pipeline.valve;

import java.util.ArrayList;

import lab.amixyue.constant.Action;
import lab.amixyue.context.*;
import lab.amixyue.model.Message;
import lab.amixyue.model.Rule;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class RecvRuleValve extends Valve {

	public RecvRuleValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvRuleValve ");
		Message msg = (Message) session.getAttribute("!@#$%^&*recvMsg");
		if (msg == null) {
			msg = session.getContext().getRecvMsg();
			if (msg != null) {
				session.setAttribute("!@#$%^&*recvMsg", msg);
			}else{
				this.broken = true;
				this.brokeStr = "Recv Buf is empty";
				log.debug(this.brokeStr);
				return;
			}
		}

		// check action before send
		boolean isAction = false;
		ArrayList<Rule> recvRules = session.getContext().getRecvRules();
		Action action = null;
		Pipeline pl = subPipe();
		// no send rules, action will stay null
		for (Rule rule : recvRules) {
			if (isAction)
				break;
			action = rule.check(msg);
			switch (action) {
			case drop:
				log.debug(msg.toString() + " :drop");
				session.setAttribute("!@#$%^&*recvMsg", null);
				this.broken = true;
				this.brokeStr = "Drop: Not real broken";
				isAction = true;
				break;
			case duplicate:
				log.debug(msg.toString() + " :duplicate");
				pl.process(session);
				// after first send, sendMsg is emptied
				session.setAttribute("!@#$%^&*recvMsg", msg);
				isAction = true;
				break;
			default:
				break;
			}
		}
		if (!isAction) {
			log.debug(msg.toString() + " :normal");
		}
	}

}
