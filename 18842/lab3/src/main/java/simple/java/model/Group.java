package simple.java.model;

import simple.java.manager.NodeManager;
import java.util.*;

public class Group {

	public final String name;
	public final String[] nodeNames;
	public enum Status{Wanted, Held, Release};
	private Status status;
	private boolean voted;
	
	private Node[] nodes;
	
	private int requestNo;
	private HashMap<Integer, Integer> requestAcks;
	//private HashMap<Integer, Integer> releaseAcks;
	private Queue<Message> requestMsgs;
	
	// send sequence of this group
	private int sendNo;
	// latest delivered message from q
	private HashMap<String, Integer> deliverNos;
	// ack count
	private HashMap<Integer, Integer> acks;
	// backup in case of loss
	private HashMap<Integer,Message> backupMsgs;
	// holdback for reack
	private HashMap<Integer,Message> holdbackMsgs;
	
	private HashMap<Integer, Message> backUpRequestMsgs;
	
	public Group(String name, String[] nodeNames){
		this.name = name;
		this.nodeNames = nodeNames;
		this.sendNo = 0;
		this.requestNo = 0;
		this.status = Status.Release;
		
		this.deliverNos = new HashMap<String, Integer>();
		this.acks = new HashMap<Integer, Integer>();
		this.backupMsgs = new HashMap<Integer,Message>();
		this.holdbackMsgs = new HashMap<Integer,Message>();
		
		this.requestAcks = new HashMap<Integer, Integer>();
		//this.releaseAcks = new HashMap<Integer, Integer>();
		this.requestMsgs = new LinkedList<Message>();
		this.backUpRequestMsgs = new HashMap<Integer, Message>();
	
		initNodes();
	}
	
	private void initNodes(){
		nodes = new Node[nodeNames.length];
		int i = 0;
		for(String nodeName : nodeNames){
			nodes[i] = NodeManager.getNode(nodeName);
			deliverNos.put(nodeName, -1);
			i++;
		}
	}
	
	public void addRequestMsg(Message msg){
		this.requestMsgs.add(msg);
	}
	
	public Message getRequestMsg(){
		Message tmp = null;
		if((tmp = this.requestMsgs.peek()) != null){
			this.requestMsgs.remove();
		}
		return tmp;
	}
	
	public void addRequestNo(){
		this.requestNo++;
	}
	
	public void addRequestNoByReq(int reqNo){
		reqNo++;
		if(this.requestNo < reqNo)
			this.requestNo = reqNo;
	}
	public int getRequestNo(){
		return this.requestNo;
	}
	
	/**
	 * update ack count, once it is full, return true
	 * @param sendNo
	 * @return
	 */
	public void setAck(int sendNo){
		acks.put(sendNo, 0);
	}
	
	public boolean addAck(int sendNo){
		int tmp = acks.get(sendNo);
		acks.put(sendNo, ++tmp);
		if(tmp == nodes.length){
			return true;
		}
		return false;
	}
	
	public void setRequestAck(int requestNo){
		requestAcks.put(requestNo, 0);
	}
	public boolean addRequestAck(int requestNo){
		int tmp = requestAcks.get(requestNo);
		requestAcks.put(requestNo, ++tmp);
		if(tmp == nodes.length){
			return true;
		}
		return false;
	}
	
//	public void setReleaseAck(int releaseNo){
//		releaseAcks.put(requestNo, 0);
//	}
//	
	public Message getBackupMsgBySendNo(int sendNo){
		return backupMsgs.get(sendNo);
	}
	
	public void addSendNo(){
		sendNo++;
	}
	
	public int getSendNo(){
		return sendNo;
	}
	
	public int getDeliverNoByName(String nodeName){
		return deliverNos.get(nodeName);
	}
	
	public void addDeliverNoByName(String nodeName){
		int tmp = deliverNos.get(nodeName);
		deliverNos.put(nodeName, tmp+1);
	}
	
	public void setDeliverNoByName(String nodeName, int sendNo){
		//int tmp = deliverNos.get(nodeName);
		deliverNos.put(nodeName, sendNo);
	}
	
	public void addAckBySendNo(int sendNo){
		int tmp = acks.get(sendNo);
		acks.put(sendNo, tmp+1);
	}
	
	public void addBackupMsg(Message msg){
		backupMsgs.put(msg.getSendNo(), msg);
	}
	
	public void addBackupRequestMsg(Message msg){
		backUpRequestMsgs.put(msg.getRequestNo(), msg);
	}
	
	public Message getBackupRequestMsg(int requestNo){
		return backUpRequestMsgs.get(requestNo);
	}
	
	public void addHoldbackMsg(Message msg){
		holdbackMsgs.put(msg.getSendNo(), msg);
	}
	
	public void reackHoldbackMsg(int sendNo){
		Message tmp = holdbackMsgs.get(sendNo);
		tmp.setAck(true);
	}
	
	public ArrayList<Message> getAvailableMsgFromHoldback(){
		ArrayList<Message> msgs = new ArrayList<Message>();
		for(Message msg: holdbackMsgs.values()){
			if(msg != null && msg.isAck()){
				msgs.add(msg);
				holdbackMsgs.remove(msg.getSendNo());
			}
		}
		return msgs;
	}
	
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isVoted() {
		return voted;
	}

	public void setVoted(boolean voted) {
		this.voted = voted;
	}
}
