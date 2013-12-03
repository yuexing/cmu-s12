package lab.amixyue.pipeline.valve;

import lab.amixyue.context.*;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class SendIdValve extends Valve {

	public SendIdValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendRuleValve ");
		Message msg = (Message) session.getAttribute("!@#$%^&*sendMsg");
		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}
		msg.setId();
	}
}
