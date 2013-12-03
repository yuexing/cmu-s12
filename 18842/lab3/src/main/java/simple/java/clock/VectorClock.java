package simple.java.clock;

import java.util.Vector;

import simple.java.manager.NodeManager;
import simple.java.model.*;

public class VectorClock implements Clock {

	private Vector<Long> vector;
	private int size;

	public VectorClock(int n) {
		size = n;
		vector = new Vector<Long>();
		for (int i = 0; i < n; i++) {
			vector.add(i, (long) 0);
		}
	}

	public void setTime(Message msg) {

	}

	public void updateTime(Message msg) {
		if (msg == null)
			return;
		VectorTimedMessage tmp = (VectorTimedMessage) msg;
		Vector<Long> tmpl = tmp.getVector();
		// do not use iterator here
		for (int i = 0; i < size; i++) {
			if (i == NodeManager.getMyindex()) {
				// nothing or ++
				long mytime = vector.get(i);
				vector.set(i, mytime + 1);
			} else {
				if (vector.get(i) < tmpl.get(i)) {
					vector.set(i, tmpl.get(i));
				}
			}
		}

	}

	public Message parse(Message msg) {
		long tmpl = vector.get(NodeManager.getMyindex());
		vector.set(NodeManager.getMyindex(), tmpl + 1);
		VectorTimedMessage tmp = new VectorTimedMessage(msg.src, msg.getDest(),
				msg.type, msg.data, vector);
		tmp.setGname(msg.getGname());
		tmp.setAck(msg.isAck());
		tmp.setDeliverNo(msg.getDeliverNo());
		tmp.setRequestNo(msg.getRequestNo());
		tmp.setSendNo(msg.getSendNo());
		// log.debug(tmp.src + ", " + tmp.getDest() + ", " + tmp.type + ", " +
		// tmp.data + ", " + vector);
		return tmp;

	}

}
