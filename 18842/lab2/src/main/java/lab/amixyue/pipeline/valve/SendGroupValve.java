package lab.amixyue.pipeline.valve;

import java.util.ArrayList;

import java.util.Map;

import lab.amixyue.constant.Global;
import lab.amixyue.context.*;
import lab.amixyue.model.*;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class SendGroupValve extends Valve {

	public SendGroupValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendGroupValve ");
		// Context context = session.getContext();
		Message msg = (Message) session.getAttribute("!@#$%^&*sendMsg");
		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}
		// check dest
		String dest = msg.getDest();
		Group g = (Group) session.getContext().getAttribute(dest);

		// dest is not a group
		if (g == null) {
			return;
		}

		// multi group now
		ArrayList<Node> nodes = g.getNodes();
		// register those who fail
		ArrayList<Node> lossers = new ArrayList<Node>();
		session.setAttribute("!@#$%^&*lossers", lossers);

		// update group status of msg
		msg.setGname(dest);

		if (session.getAttribute(Global.prefix + "groupcast")!=null 
				&& (Boolean) session.getAttribute(Global.prefix + "groupcast")) {
			//do nothing
		}else{
			@SuppressWarnings("unchecked")
			Map<String, Integer> gids = (Map<String, Integer>) session
					.getContext().getAttribute("!@#$%^&*gids");
			msg.setGid(gids.get(dest));
			gids.put(dest, msg.getGid() + 1);
			session.getContext().setAttribute("!@#$%^&*gids", gids);
		}

		for (Node n : nodes) {
			// don's send to myself
			// or the time will be weird
			if (!n.getName().equals(session.getContext().getMeNode().getName())) {
				msg.setDest(n.getName());
				session.setAttribute("!@#$%^&*sendMsg", msg);
				subPipe().process(session);
			}
		}

		// lossers
		log.debug("Unsuccessful Multicast:");
		for (Node n : lossers) {
			log.debug(n);
		}

		// msg has been all sent
		this.broken = true;
		this.brokeStr = "Not real broken: Mulicast all";
	}
}
