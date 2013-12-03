package simple.java.manager;

import java.util.*;
import simple.java.model.Message;

public class BufferManager {

	public static Queue<Message> sendDelayQueue;
	public static Queue<Message> recvDelayQueue;
	public static Queue<Message> recvQueue;
	
	static{
		sendDelayQueue = new LinkedList<Message>();
		recvDelayQueue = new LinkedList<Message>();
		recvQueue = new LinkedList<Message>();
	}
	
	public static void addRecv(Message msg){
		recvQueue.add(msg);
	}
	
	public static void addAllRecv(ArrayList<Message> msgs){
		recvQueue.addAll(msgs);
	}
	public static void addSendDelay(Message msg){
		sendDelayQueue.add(msg);
	}
	
	public static void addRecvDelay(Message msg){
		recvDelayQueue.add(msg);
	}
	
	public static void clear(){
		sendDelayQueue.clear();
		recvDelayQueue.clear();
		recvQueue.clear();
	}
	
	public static Message getSendDelay(){
		Message msg = sendDelayQueue.peek();
		if(msg != null)
			sendDelayQueue.remove();
		return msg;
	}
	
	public static Message getRecvDelay(){
		Message msg = recvDelayQueue.peek();
		if(msg != null)
			recvDelayQueue.remove();
		return msg;
	}
	
	public static Message getRecv(){
		Message msg = recvQueue.peek();
		if(msg != null)
			recvQueue.remove();
		return msg;
	}
}
