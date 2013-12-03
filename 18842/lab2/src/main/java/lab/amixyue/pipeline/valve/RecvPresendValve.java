package lab.amixyue.pipeline.valve;


import lab.amixyue.constant.Global;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class RecvPresendValve extends Valve {

	public RecvPresendValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter RecvGMsgChkValve ");
		Message msg = (Message)session.getAttribute("!@#$%^&*recvMsg");
		
		//as trick for ack
		if(msg == null)
			return;
		
		if(msg.getGname() == null){
			broken = true;
			brokeStr = "Not real break! node recv";
			return;
		}
		
		//for broad cast
		//no need to change gid
		session.setAttribute(Global.prefix+"groupcast", true);
		msg.setSrc(session.getContext().getMeNode().getName());
		msg.setDest(msg.getGname());
		session.setAttribute("!@#$%^&*sendMsg", msg);
	}

}
