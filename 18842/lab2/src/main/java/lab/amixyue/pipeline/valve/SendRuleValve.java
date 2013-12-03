package lab.amixyue.pipeline.valve;

import java.util.ArrayList;

import lab.amixyue.constant.Action;
import lab.amixyue.context.*;
import lab.amixyue.model.*;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class SendRuleValve extends Valve{
	
	public SendRuleValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendRuleValve ");
		Message msg = (Message)session.getAttribute("!@#$%^&*sendMsg");
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}
		
		//check action before send
		boolean isAction = false;
		ArrayList<Rule> sendRules = session.getContext().getSendRules();
		Action action = null;
		Pipeline pl = subPipe();
		//no send rules, action will stay null
		for (Rule rule : sendRules) {
			if (isAction) break;
			action = rule.check(msg);
			switch (action) {
			case drop:
				log.debug(msg.toString() + " :drop");	
				session.setAttribute("!@#$%^&*sendMsg", null);
				this.broken = true;
				this.brokeStr = "Drop: Not real broken";
				isAction = true;
				break;
			case delay://
				log.debug(msg.toString() + " :delay");
				session.getContext().addDelayMsg(msg);
				session.setAttribute("!@#$%^&*sendMsg", null);
				this.broken = true;
				this.brokeStr = "Delay: Not real broken";
				isAction = true;
				break;
			case duplicate:
				log.debug(msg.toString() + " :duplicate");
				pl.process(session);
				sendDelay(session);
				//after first send, sendMsg is emptied
				session.setAttribute("!@#$%^&*sendMsg", msg);
				isAction = true;
				break;
			default:
				break;
			}
		}
		if (!isAction){
			log.debug(msg.toString() + " :normal");
		}
	}	

	private void sendDelay(Session session){
		Message msg = null;
		Pipeline pl = subPipe();
		while((msg = session.getContext().getDelayMsg())!=null){
			session.setAttribute("!@#$%^&*sendMsg", msg);
			pl.process(session);
		}
	}
}
