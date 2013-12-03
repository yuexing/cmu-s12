package simple.java.service;

import lombok.extern.log4j.Log4j;
import simple.java.manager.*;
import simple.java.model.*;
import simple.java.model.Message.MessageType;
import simple.java.model.Rule.Action;
import java.util.*;

@Log4j
public class MessagePasser {

	private ArrayList<Message> buf;
	private static MessagePasser instance;

	private MessagePasser() {
		buf = new ArrayList<Message>();
	}

	public static synchronized MessagePasser getMessagePasser() {
		if (instance == null)
			instance = new MessagePasser();
		return instance;
	}

	public void send(Message msg) {
		boolean isGroupMessage = false;
		Group g = GroupManager.getGroupByName(msg.getDest());

		if (msg.getGname() != null) {
			isGroupMessage = true;
		}
		
		msg.setId();
		Message timedMsg = ClockManager.clock.parse(msg);
		Action action = RuleManager.checkSend(timedMsg);

		switch (action) {
		case drop:
			//
			if (timedMsg instanceof VectorTimedMessage) {
				log.debug("drop: " + (VectorTimedMessage) timedMsg);
			} else if (timedMsg instanceof LogicTimedMessage) {
				log.debug("drop: " + (LogicTimedMessage) timedMsg);
			}
			break;
		case delay:
			if (timedMsg instanceof VectorTimedMessage) {
				log.debug("delay: " + (VectorTimedMessage) timedMsg);
			} else if (timedMsg instanceof LogicTimedMessage) {
				log.debug("delay: " + (LogicTimedMessage) timedMsg);
			}
			BufferManager.addSendDelay(timedMsg);
			break;
		case duplicate:
			if (timedMsg instanceof VectorTimedMessage) {
				log.debug("duplicate: " + (VectorTimedMessage) timedMsg);
			} else if (timedMsg instanceof LogicTimedMessage) {
				log.debug("duplicate: " + (LogicTimedMessage) timedMsg);
			}

			if (!isGroupMessage)
				Sender.send(timedMsg);
			else {
				if(timedMsg.type.equals(MessageType.request)){
					g.setRequestAck(0);
				}
//				else if(timedMsg.type.equals(MessageType.release)){
//					g.setReleaseAck(0);
//				}
				g.setAck(timedMsg.getSendNo());
				g.addBackupMsg(timedMsg);
				GroupSender.send(timedMsg);
			}
			Message tmp = null;
			while ((tmp = BufferManager.getSendDelay()) != null) {
				if (tmp.getGname() == null)
					Sender.send(tmp);
				else {
					if(tmp.type.equals(MessageType.request)){
						g.setRequestAck(0);
					}
//					else if(tmp.type.equals(MessageType.release)){
//						g.setReleaseAck(0);
//					}
					g.setAck(tmp.getSendNo());
					g.addBackupMsg(tmp);
					GroupSender.send(tmp);
				
				}
			}

			if (!isGroupMessage)
				Sender.send(timedMsg);
			else {
				if(timedMsg.type.equals(MessageType.request)){
					g.setRequestAck(0);
				}
//				else if(timedMsg.type.equals(MessageType.release)){
//					g.setReleaseAck(0);
//				}
				g.setAck(timedMsg.getSendNo());
				g.addBackupMsg(timedMsg);
				GroupSender.send(timedMsg);				
			}
			break;
		case none:
			if (timedMsg instanceof VectorTimedMessage) {
				log.debug("normal: " + (VectorTimedMessage) timedMsg);
			} else if (timedMsg instanceof LogicTimedMessage) {
				log.debug("normal: " + (LogicTimedMessage) timedMsg);
			}
			if (!isGroupMessage)
				Sender.send(timedMsg);
			else {
				if(timedMsg.type.equals(MessageType.request)){
					g.setRequestAck(0);
				}
//				else if(timedMsg.type.equals(MessageType.release)){
//					g.setReleaseAck(0);
//				}
				g.setAck(timedMsg.getSendNo());
				g.addBackupMsg(timedMsg);
				GroupSender.send(timedMsg);
			}
			while ((tmp = BufferManager.getSendDelay()) != null) {
				if (tmp.getGname() == null)
					Sender.send(tmp);
				else {
					if(tmp.type.equals(MessageType.request)){
						g.setRequestAck(0);
					}
//					else if(tmp.type.equals(MessageType.release)){
//						g.setReleaseAck(0);
//					}
					g.setAck(tmp.getSendNo());
					g.addBackupMsg(tmp);
					GroupSender.send(tmp);
				}
			}
			break;
		default:
			log.debug("weird!");
		}
	}

	public ArrayList<Message> recv() {
		buf.clear();

		// get available group msg first
		ArrayList<Message> msgs = GroupManager.getAvailableMsgs();
		
		BufferManager.addAllRecv(msgs);

		Message msg = BufferManager.getRecv();

		ClockManager.clock.updateTime(msg);

		buf.add(msg);

		while ((msg = BufferManager.getRecvDelay()) != null) {
			if (msg.getGname() != null) {
				GroupMessageRecvProcessor.process(msg);
				continue;
			}
			buf.add(msg);
			ClockManager.clock.updateTime(msg);
		}
		log.debug(buf);
		return buf;
	}
}
