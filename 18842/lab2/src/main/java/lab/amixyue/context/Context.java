package lab.amixyue.context;

import lab.amixyue.clock.ClockService;
import lab.amixyue.constant.*;
import lab.amixyue.context.Session;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lab.amixyue.model.Message;
import lab.amixyue.model.Node;
import lab.amixyue.model.Rule;
/**
 * A representation of a monitor who is in charge
 * for synchronization.
 * <p>
 * @author amy
 *
 */
public interface Context {

	//for init
	public final static Lock readinit = new ReentrantReadWriteLock(true)
	.readLock();
	public final static Lock writeinit = new ReentrantReadWriteLock(true)
	.writeLock();
	
	public Node getMeNode();

	public int getMeIndex() ;
	
	public ClockService getClock();

	public Queue<Message> getSendMsgs();
	
	public void addSendMsg(Message msg);
	
	public Message getSendMsg();
	
	public  Queue<Message> getDelayMsgs();
	
	public void addDelayMsg(Message msg);
	
	public Message getDelayMsg();
	
	public  Queue<Message> getRecvMsgs() ;
	
	/**
	 * now just for multicast and believe to have more usage
	 * @param multi
	 * @return
	 */
	public  Queue<Message> getRecvMsgs(MessageType type) ;
	
	public void addRecvMsg(Message msg);
	
	public Message getRecvMsg();

	public  ArrayList<Node> getNodes() ;

	public  ArrayList<Rule> getSendRules();

	public  ArrayList<Rule> getRecvRules();
	
	public void clearBasic();
	
	public void clearMsgs();
	
	public void setAttribute(String name, Object value);
	
	public Object getAttribute(String name);
	
	public Session getSession();
}
