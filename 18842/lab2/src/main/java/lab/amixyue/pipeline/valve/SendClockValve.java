package lab.amixyue.pipeline.valve;

import lab.amixyue.context.*;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class SendClockValve extends Valve {

	public SendClockValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter ClockValve ");
		Message msg = (Message) session.getAttribute("!@#$%^&*sendMsg");
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null. Weird";
			log.debug(this.brokeStr);
			return;
		}
		Message nMsg = session.getContext().getClock().setTime(msg);
		session.setAttribute("!@#$%^&*sendMsg", nMsg);
	}
}
