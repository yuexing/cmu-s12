package lab.amixyue.context;

import lab.amixyue.clock.ClockService;
import lab.amixyue.constant.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lab.amixyue.constant.Protocal;
import lab.amixyue.model.Message;
import lab.amixyue.model.Node;
import lab.amixyue.model.Rule;
import lombok.*;

/**
 * Apply Design Pattern: Builder as the product is the same, however, the way to
 * produce is different.
 * <p>
 * To make sure no one except ContextBuilder and its subclass can produce a
 * {@link Context}, make it the inner class. Also, kind of Singleton.
 * <p>
 * Apply Design Pattern: Adapter. As {@link Context} is what we expect, so adapt
 * {@link ContextBuilder} to {@link Context}
 * <p>
 * Apply Java reader-writer lock: main: sendMsg, delayMsg recv: recvMsg
 * FileSchedule: init all
 * 
 * @author amy
 * 
 */
public abstract class AbstractContext implements Context, ContextBuilder {
	@Getter
	protected String name;

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Lock readLock = lock.readLock();
	private final Lock readSLock = lock.readLock();
	private final Lock readDLock = lock.readLock();
	private final Lock readRLock = lock.readLock();

	private final Lock writeLock = lock.writeLock();
	private final Lock writeSLock = lock.writeLock();
	private final Lock writeDLock = lock.writeLock();
	private final Lock writeRLock = lock.writeLock();

	public abstract void buildContext();

	private static class Session implements lab.amixyue.context.Session{
		private lab.amixyue.context.Context context;

		// should also be static, as all will share
		// should consider adding session in the future
		Map<String, Object> map;

		public Session(lab.amixyue.context.Context context) {
			this.context = context;
			map = new HashMap<String, Object>();
		}

		public lab.amixyue.context.Context getContext() {
			return this.context;
		}

		public void setAttribute(String name, Object value) {
			map.put(name, value);
		}

		public Object getAttribute(String name) {
			if(map.containsKey(name)){
				return map.get(name);
			}
			else 
				return null;
		}
		
		
	}

	public Session getSession() {
		return new Session(this);
	}

	public ClockService getClock() {
		return Context.clock;
	}

	public Node getMeNode() {
		try {
			readinit.lock();
			readLock.lock();
			return Context.meNode;
		} finally {
			readLock.unlock();
			readinit.unlock();
		}
	}

	public int getMeIndex() {
		try {
			readinit.lock();
			readLock.lock();
			return Context.meIndex;
		} finally {
			readDLock.unlock();
			readinit.unlock();
		}
	}

	public Queue<Message> getSendMsgs() {
		try {
			readinit.lock();
			readSLock.lock();
			return Context.sendMsgs;
		} finally {
			readSLock.unlock();
			readinit.unlock();
		}
	}

	public Queue<Message> getDelayMsgs() {
		try {
			readinit.lock();
			readDLock.lock();
			return Context.delayMsgs;
		} finally {
			readDLock.unlock();
			readinit.unlock();
		}
	}

	public Queue<Message> getRecvMsgs() {
		try {
			readinit.lock();
			readRLock.lock();
			return Context.recvMsgs;
		} finally {
			readRLock.unlock();
			readinit.unlock();
		}
	}

	public Queue<Message> getRecvMsgs(MessageType type) {
		Queue<Message> tmpMsgs;
		Message tmp;
		try {
			readinit.lock();
			readRLock.lock();
			tmpMsgs = new LinkedList<Message>();
			while ((tmp = Context.recvMsgs.peek()) != null) {
				if (tmp.getType().equals(type)) {
					Context.recvMsgs.remove(tmp);
					tmpMsgs.add(tmp);
				}
			}
		} finally {
			readRLock.unlock();
			readinit.unlock();
		}
		return tmpMsgs;
	}

	public ArrayList<Node> getNodes() {
		try {
			readinit.lock();
			readLock.lock();
			return Context.nodes;
		} finally {
			readLock.unlock();
			readinit.unlock();
		}
	}

	public ArrayList<Rule> getSendRules() {
		try {
			readinit.lock();
			readLock.lock();
			return Context.sendRules;
		} finally {
			readLock.unlock();
			readinit.unlock();
		}
	}

	public ArrayList<Rule> getRecvRules() {
		try {
			readinit.lock();
			readLock.lock();
			return Context.recvRules;
		} finally {
			readLock.unlock();
			readinit.unlock();
		}
	}

	public void setAttribute(String name, Object value) {
		try {
			writeLock.lock();
			Context.contextMap.put(name, value);
		} finally {
			writeLock.unlock();
		}
	}

	public Object getAttribute(String name) {
		try {
			readLock.lock();
			if (Context.contextMap.containsKey(name))
				return Context.contextMap.get(name);
			else
				return null;
		} finally {
			readLock.unlock();
		}

	}

	public void addSendMsg(Message msg) {
		try {
			readinit.lock();
			writeSLock.lock();
			Context.sendMsgs.add(msg);
		} finally {
			writeSLock.unlock();
			readinit.unlock();
		}
	}

	public Message getSendMsg() {
		try {
			readinit.lock();
			readSLock.lock();
			Message m = Context.sendMsgs.peek();
			if (m != null)
				Context.sendMsgs.remove();
			return m;
		} finally {
			readSLock.unlock();
			readinit.unlock();
		}

	}

	public void addDelayMsg(Message msg) {
		try {
			readinit.lock();
			writeDLock.lock();
			Context.delayMsgs.add(msg);
		} finally {
			writeDLock.unlock();
			readinit.unlock();
		}
	}

	public Message getDelayMsg() {
		try {
			readinit.lock();
			readDLock.lock();
			Message m = Context.delayMsgs.peek();
			if (m != null)
				Context.delayMsgs.remove();
			return m;
		} finally {
			readDLock.unlock();
			readinit.unlock();
		}

	}

	public void addRecvMsg(Message msg) {
		try {
			readinit.lock();
			writeRLock.lock();
			Context.recvMsgs.add(msg);
		} finally {
			writeRLock.unlock();
			readinit.unlock();
		}
	}

	public Message getRecvMsg() {
		try {
			readinit.lock();
			readRLock.lock();
			Message m = Context.recvMsgs.peek();
			if (m != null)
				Context.recvMsgs.remove();
			return m;
		} finally {
			readRLock.unlock();
			readinit.unlock();
		}
	}

	public void clearBasic() {
		// try{
		// writeLock.lock();
		Context.nodes.clear();
		Context.sendRules.clear();
		Context.recvRules.clear();
		// }finally{
		// writeLock.unlock();
		// }
	}

	public void clearMsgs() {
		// try{
		// writeSLock.lock();
		// writeDLock.lock();
		// writeRLock.lock();
		Context.sendMsgs.clear();
		Context.recvMsgs.clear();
		Context.delayMsgs.clear();
		Context.contextMap.clear();
		// }finally{
		// writeSLock.unlock();
		// writeDLock.unlock();
		// writeRLock.unlock();
		// }
	}

	/**
	 * make {@link Context} a structure static for saving storage final in case
	 * of error act
	 * 
	 * @author amy
	 * 
	 */
	@ToString
	protected static class Context {
		// make it a hash map(name: node)
		static final ArrayList<Node> nodes = new ArrayList<Node>();;

		final static ArrayList<Rule> sendRules = new ArrayList<Rule>();

		final static ArrayList<Rule> recvRules = new ArrayList<Rule>();
		// a stub
		final static Node meNode = new Node("name", "ip", 18842, Protocal.tcp);

		static int meIndex; // in nodes

		static Protocal protocal;

		static ClockService clock;

		final static Queue<Message> sendMsgs = new LinkedList<Message>();

		final static Queue<Message> delayMsgs = new LinkedList<Message>();

		final static Queue<Message> recvMsgs = new LinkedList<Message>();

		final static Map<String, Object> contextMap = new HashMap<String, Object>();

		public Context() {

		}

	}
}
