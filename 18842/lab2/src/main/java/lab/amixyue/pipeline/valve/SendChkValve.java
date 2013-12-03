package lab.amixyue.pipeline.valve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lab.amixyue.constant.GStatus;
import lab.amixyue.constant.Global;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.model.Group;
import lab.amixyue.model.Message;
import lab.amixyue.model.Node;
import lab.amixyue.pipeline.Pipeline;
import lab.amixyue.util.NodeUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class SendChkValve extends Valve {

	public SendChkValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter SendChkValve ");
		Context context = session.getContext();
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
			ArrayList<Node> all = ((Group) context.getAttribute(Global.prefix
					+ "broad")).getNodes();

			for (Node n : all) {
				if (n.getName().equals(dest)) {
					@SuppressWarnings("unchecked")
					Map<String, HashSet<String>> nng = (HashMap<String, HashSet<String>>) session
							.getContext().getAttribute("!@#$%^&*nng");

					if (nng.containsKey(dest)) {
						// all group names of the node
						HashSet<String> gs = nng.get(dest);
						for (String gname : gs) {
							// name -> group
							Group greal = (Group) session.getContext()
									.getAttribute(gname);
							// active not send
							if (greal.getStatus().equals(GStatus.active)) {
								this.broken = true;
								this.brokeStr = "The Node is in an active Group";
								log.debug(this.brokeStr);
								return;
							}// not in an active group
						}
					}// not in group
					return;
				}
			}
			this.broken = true;
			this.brokeStr = "No such node!";
			log.debug(this.brokeStr);
			return;

		}

		// dest is a group, but i am not in
		if (!NodeUtil.NnNs(session.getContext().getMeNode(), g.getNodes())) {
			this.broken = true;
			this.brokeStr = "I am not in the group";
			log.debug(this.brokeStr);
			return;
		}

		// dest is a group and i am in
		// but not start
		if (g.getStatus().equals(GStatus.idle)) {
			this.broken = true;
			this.brokeStr = "Group is idle";
			log.debug(this.brokeStr);
			return;
		}

	}

}
