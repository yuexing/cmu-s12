package lab.amixyue.pipeline.valve;

import lab.amixyue.constant.Global;
import lab.amixyue.context.*;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class RecvValve extends Valve {

	public RecvValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvValve ");
		Message msg = (Message) session.getAttribute(Global.prefix + "recvMsg");
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null. Weird";
			log.debug(this.brokeStr);
			return;
		}
		
		//return to client
		session.getContext().setAttribute(Global.prefix + "recvMsg", msg);
		log.debug("recvd: " + msg);
	}

}
