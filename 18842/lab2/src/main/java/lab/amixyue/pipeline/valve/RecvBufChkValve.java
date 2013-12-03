package lab.amixyue.pipeline.valve;

import lab.amixyue.constant.Global;
import lab.amixyue.constant.MessageType;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class RecvBufChkValve extends Valve {

	public RecvBufChkValve(Pipeline curPipe) {
		super(curPipe);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvBufChkValve ");
		Context context = session.getContext();
		Message msg = (Message)session.getAttribute(Global.prefix+"recvMsg");
		//session.setAttribute(Global.prefix+"recvMsg", null);
		
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}
		
		if(msg.getType().equals(MessageType.request))return;
		if(msg.getType().equals(MessageType.ack))return;
		if(msg.getType().equals(MessageType.release))return;
		
		context.addRecvMsg(msg);
		log.debug("Recv to Buffer: " +msg);
		}
}
