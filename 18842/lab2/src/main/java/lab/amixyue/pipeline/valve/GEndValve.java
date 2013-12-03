package lab.amixyue.pipeline.valve;

import lab.amixyue.constant.GStatus;
import lab.amixyue.constant.Global;
import lab.amixyue.context.*;
import lab.amixyue.model.Group;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;
@Log4j
public class GEndValve extends Valve{
	
	public GEndValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter GEndValve ");
		Message msg = (Message)session.getAttribute(Global.prefix+"sendMsg");
		
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}
		
		//check group
		String gname = msg.getDest();
		Group g = (Group) session.getContext().getAttribute(gname);
		
		if(g == null){
			this.broken = true;
			this.brokeStr = "No Such Group";
			log.fatal(this.brokeStr);
			return;
		}
		
		//update status
		g.setStatus(GStatus.idle);
		
		//update msg
		//no need to change gid
		session.setAttribute(Global.prefix+"groupcast", true);
		msg.setData(g);
		msg.setDest(Global.prefix+"broad");
		session.setAttribute(Global.prefix+"sendMsg", msg);
	}
}
