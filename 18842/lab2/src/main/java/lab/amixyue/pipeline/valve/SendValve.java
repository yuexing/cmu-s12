package lab.amixyue.pipeline.valve;

import java.util.ArrayList;
import lab.amixyue.context.*;
import lab.amixyue.model.*;
import lab.amixyue.pipeline.Pipeline;
import lab.amixyue.send.SendController;
import lab.amixyue.send.SendCtrlFactory;
import lab.amixyue.util.NodeUtil;
import lombok.extern.log4j.Log4j;
@Log4j
public class SendValve extends Valve {

	public SendValve(Pipeline curPipe) {
		super(curPipe);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(Session session) {
		log.debug("Enter SendValve ");
		Message msg =(Message) session.getAttribute("!@#$%^&*sendMsg");		
		session.setAttribute("!@#$%^&*sendMsg", null);
		
		if(msg==null) {
			this.broken = true;
			this.brokeStr = "Message is null. Weird";
			log.debug(this.brokeStr);
			return;
		}else{		
			
			//has to consider clock type
//			if(ClockFactory.getClock().getType().equals(ClockType.logic))
//			 msg = (LogicTimeMessage)msg;
//			else if(ClockFactory.getClock().getType().equals(ClockType.vector))
//				msg = (VectorClockMessage)msg;
			
			ArrayList<Node> nodes = session.getContext().getNodes();
			Node tmpNode = NodeUtil.getNodeByName(nodes, msg.getDest());
			
			if(tmpNode == null){
				this.broken = true;
				this.brokeStr = "No dest found!";
				log.debug(this.brokeStr);
				return;
			}
			
			SendController sendCtrl = SendCtrlFactory.getSendCtrl(tmpNode.getProtocal());
			
			boolean sent = sendCtrl.send(tmpNode.getIp(), tmpNode.getPort(), msg);
			if(!sent && session.getAttribute("!@#$%^&*lossers")!=null){
				((ArrayList<Node>) session.getAttribute("!@#$%^&*lossers")).add(tmpNode);
			}
		}
	}
}
