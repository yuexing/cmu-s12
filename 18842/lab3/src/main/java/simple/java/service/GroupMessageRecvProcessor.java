package simple.java.service;

import lombok.extern.log4j.Log4j;
import simple.java.manager.GroupManager;
import simple.java.manager.NodeManager;
import simple.java.model.*;
import simple.java.model.Group.Status;
import simple.java.model.Message.MessageType;
@Log4j
public class GroupMessageRecvProcessor {

	/**
	 * gack, reack should not be counted into send NO.
	 * @param msg
	 */
	public static void process(Message msg){
		
		String gname = msg.getGname();
		Group group = GroupManager.getGroupByName(gname);
		int sendNo = msg.getSendNo();
		int requestNo = msg.getRequestNo();
		
		if(msg.type.equals(MessageType.gack)){
			// == n
			log.debug( "#" + msg.getSendNo() + " "+ msg.src + " has gacked");
			if(group.addAck(sendNo)){
				log.debug( "#" + msg.getSendNo() + " reack sending...");
				Message reack = new Message(NodeManager.getMyname(), 
						gname, MessageType.reack, null);
				reack.setGname(gname);
				reack.setSendNo(sendNo);
				GroupSender.send(reack);
			}
			return;
		}else if(msg.type.equals(MessageType.reack)){
			log.debug( "#" + msg.getSendNo() +" has reacked");
			group.reackHoldbackMsg(sendNo);
		}else if(msg.type.equals(MessageType.request)){
			//add request No for the group
			group.addRequestNoByReq(msg.getRequestNo());
			if(group.getStatus().equals(Status.Held) || group.isVoted()){
				log.debug( "req#" + msg.getRequestNo() +" add to queue");
				group.addRequestMsg(msg);
			}else{
				group.setVoted(true);
				Message tmp = new Message(NodeManager.getMyname(), msg.src, MessageType.ack, null);
				tmp.setGname(gname);
				tmp.setRequestNo(requestNo);
				log.debug( "req#" + msg.getRequestNo() +" ack ...");
				Sender.send(tmp);
			}
		}else if(msg.type.equals(MessageType.ack)){
			log.debug( "req#" + msg.getRequestNo() + " "+ msg.src +" has acked");
			
			if(group.addRequestAck(requestNo)){
				log.debug( "req#" + msg.getRequestNo() +" all acked");
				group.setStatus(Status.Held);
				
				Message m = group.getBackupRequestMsg(msg.getRequestNo());
				
				Resource rs = (Resource) m.data;
				//Resource rs = Resource.printer;
				rs.process(NodeManager.getMyname());
				Message tmp = new Message(NodeManager.getMyname(), gname, MessageType.release, null);
				tmp.setRequestNo(requestNo);
				tmp.setGname(gname);
				GroupSender.send(tmp);
				group.setStatus(Status.Release);
			}
		}else if(msg.type.equals(MessageType.release)){
			log.debug( "req#" + msg.getRequestNo() +" has released");
			Message tmp = group.getRequestMsg();
			if(tmp != null){
				Message ack = new Message(NodeManager.getMyname(), tmp.src, MessageType.ack, null);
				ack.setRequestNo(tmp.getRequestNo());
				ack.setGname(gname);
				log.debug( "req#" + ack.getRequestNo() +" ack ...");
				Sender.send(ack);
				group.setVoted(true);
			}else{
				log.debug( "request queue empty!");
				group.setVoted(false);
			}
		}else if(msg.type.equals(MessageType.gask)){
			String retransmit = (String) msg.data;
			String[] retraVars = retransmit.split(",");
			String logStr = "Retransmit: ";
			for(int i =0; i < retraVars.length; i++){
				logStr += i + ",";
				Message tmp = group.getBackupMsgBySendNo(i);
				tmp.setDest(msg.src);
				Sender.send(tmp);
			}
			log.debug(logStr);
		}
		else{
			//gack first
			int expectDeliverNo = group.getDeliverNoByName(msg.src)+1;
			log.debug( "#" + msg.getSendNo() +" add to holdback ...");
			group.addHoldbackMsg(msg);
			log.debug( "#" + msg.getSendNo() +" gack ...");
			Message gack = new Message(NodeManager.getMyname(), msg.src, MessageType.gack, null);
			gack.setGname(gname);
			gack.setSendNo(sendNo);
			Sender.send(gack);
			
			if(sendNo < expectDeliverNo){
				//must be retransmit
				//nothing
			}else if(sendNo == expectDeliverNo){
				//that is it!
				group.setDeliverNoByName(msg.src, sendNo);
			}else{
				group.setDeliverNoByName(msg.src, sendNo);
				//ask for retransmit
				String retransmit = "";
				for(int i = expectDeliverNo; i < sendNo; i++){
					retransmit += i+",";
				}
				Message gask = new Message(NodeManager.getMyname(), msg.src, MessageType.gask, retransmit);
				gask.setGname(gname);
				Sender.send(gask);
				log.debug("Ask For Retransmit: " +retransmit);
			}
//			if(msg.getSendNo() == group.getDeliverNoByName(msg.src) + 1){
//				log.debug( "#" + msg.getSendNo() +" add to holdback ...");
//				group.addHoldbackMsg(msg);
//				group.addDeliverNoByName(msg.src);
//				log.debug( "#" + msg.getSendNo() +" gack ...");
//				Message gack = new Message(NodeManager.getMyname(), msg.src, MessageType.gack, null);
//				gack.setGname(gname);
//				gack.setSendNo(sendNo);
//				Sender.send(gack);
//			}
//			// chk if sender miss some msg
//			if((msg.getDeliverNo() < group.getSendNo() - 1) && !msg.src.equals(NodeManager.getMyname())){
//				int i = msg.getDeliverNo();
//				if(i == -1){
//					i = 0;
//				}
//				log.debug( msg.src + " miss msg " + i + " - " + (group.getSendNo()-1));
//				Message tmp = null;
//				while (i < group.getSendNo()){
//					tmp = group.getBackupMsgBySendNo(i);
//					tmp.setDest(msg.src);
//					Sender.send(tmp);
//					i++;
//				}
//			}
			
		}//normal group msg
	}
}
