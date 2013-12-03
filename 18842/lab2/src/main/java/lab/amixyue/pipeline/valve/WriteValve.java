package lab.amixyue.pipeline.valve;

import lab.amixyue.constant.MessageType;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class WriteValve extends Valve {

	public WriteValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter WriteValve ");
		Message msg = (Message)session.getAttribute("!@#$%^&*sendMsg");
		
		//if write - > request
		if(msg.getType().equals(MessageType.write)){
			msg.setType(MessageType.request);
		}
		session.setAttribute("!@#$%^&*sendMsg", msg);
	}

}
