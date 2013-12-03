package lab.amixyue.pipeline.valve;

import java.util.*;

import lab.amixyue.constant.Global;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Message;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class RecvChkValve extends Valve {

	public RecvChkValve(Pipeline curPipe) {
		super(curPipe);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Session session) {

		Context context = session.getContext();
		Message msg = (Message) session.getAttribute(Global.prefix + "recvMsg");
		log.debug("Enter RecvGMsgChkValve " + msg);
		// session.setAttribute(Global.prefix+"recvMsg", null);

		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.fatal(this.brokeStr);
			return;
		}

		if (msg.getGname() == null)
			return;

		// check me & group
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> nng = (Map<String, Set<String>>) context
				.getAttribute(Global.prefix + "nng");
		if (!nng.get(context.getMeNode().getName()).contains(msg.getGname())) {
			this.broken = true;
			this.brokeStr = "I am not in this group!";
			log.debug(this.brokeStr);
			return;
		}

		// check shortest path first
		@SuppressWarnings("unchecked")
		Map<String, String> spath = (Map<String, String>) context
				.getAttribute(Global.prefix + "spath");
		if (msg.getSrc().equals(spath.get(msg.getGname()))) {
			// shortest and do nothing
		} else {
			this.broken = true;
			this.brokeStr = "Message not from shortest path";
			log.debug(this.brokeStr);
			return;
		}

		// check gid
		@SuppressWarnings("unchecked")
		Map<String, Integer> gids = (HashMap<String, Integer>) context
				.getAttribute(Global.prefix + "gids");
		if (msg.getGid() != gids.get(msg.getGname())) {
			int expectId = gids.get(msg.getGname());
			if (msg.getGid() < expectId) {
				log.debug("Group " + msg.getGname() + " MessageId: "
						+ msg.getGid() + "delayed!");
			} else {
				if (!msg.getGname().equals(Global.prefix + "broad")) {
					gids.put(msg.getGname(), msg.getGid() + 1);
					log.error("Group " + msg.getGname() + " MessageId: "
							+ expectId + "Missed!");
				}
			}
			this.broken = true;
			this.brokeStr = "unexpected Message ID";
			log.debug(this.brokeStr);
			return;
		} else {
			gids.put(msg.getGname(), msg.getGid() + 1);
		}

		msg.setDest(msg.getGname());
	}

}
