package lab.amixyue.pipeline.valve;

import lab.amixyue.context.*;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lab.amixyue.util.ObjnByte;
import lombok.extern.log4j.Log4j;
@Log4j
public class SendMsgCheckValve extends Valve{
	
	public SendMsgCheckValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendMsgCheckValve ");
		Message msg = (Message)session.getAttribute("!@#$%^&*sendMsg");
		
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}
		
//		if(msg.getSrc()==null||msg.getDest()==null
//				||msg.getType()==null||msg.getData()==null){
//			this.broken = true;
//			this.brokeStr = "Message init with null value";
//			log.fatal(this.brokeStr);
//			return;
//		}
		
		if(ObjnByte.toByteArray(msg.getData()).length > 65000){
			this.broken = true;
			this.brokeStr = "Message init with too large size";
			log.fatal(this.brokeStr);
			return;
		}
	}
}
