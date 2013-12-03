package lab.amixyue.pipeline.valve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lab.amixyue.constant.Global;
import lab.amixyue.context.*;
import lab.amixyue.model.Group;
import lab.amixyue.model.Message;
import lab.amixyue.model.Node;
import lab.amixyue.pipeline.Pipeline;
import lombok.extern.log4j.Log4j;

@Log4j
public class UpCtxtByGroupValve extends Valve {

	public UpCtxtByGroupValve(Pipeline curPipe) {
		super(curPipe);
	}

	@Override
	public void process(Session session) {
		log.debug("Enter UpCtxtByGroupValve ");
		// group msg
		Message msg = (Message) session.getAttribute(Global.prefix+"sendMsg");
		if (msg == null) {
			this.broken = true;
			this.brokeStr = "Message is null probably for init";
			log.debug(this.brokeStr);
			return;
		}

		Group g = (Group) msg.getData();
		session.getContext().setAttribute(g.getName(), g);
		ArrayList<Node> mems = g.getNodes();

		
		// add hashmap spath group name: name
		@SuppressWarnings("unchecked")
		final
		Map<String, Integer> distance = (Map<String, Integer>) session.getContext()
				.getAttribute(Global.prefix+"distance");
		ArrayList<String> nnames = new ArrayList<String>();
		
		// add hashmap nng name: group names
		@SuppressWarnings("unchecked")
		Map<String, HashSet<String>> nng = (HashMap<String, HashSet<String>>) session.getContext()
				.getAttribute(Global.prefix+"nng");
		@SuppressWarnings("unchecked")
		Map<String, String> spath = (HashMap<String, String>) session.getContext()
				.getAttribute(Global.prefix+"spath");

//		if (nng == null) {
//			nng = new HashMap<String, HashSet<String>>();
//			session.getContext().setAttribute(Global.prefix+"nng", nng);
//			log.debug("create node&group table.");
//		}
//
//		if (spath == null) {
//			spath = new HashMap<String, String>();
//			session.getContext().setAttribute(Global.prefix+"spath", spath);
//			log.debug("create group&spath table.");
//		}

		for (Node n : mems) {
			String nname = n.getName();
			nnames.add(nname);
			if (nng.containsKey(nname))
				nng.get(nname).add(g.getName());
			else {
				HashSet<String> gs = new HashSet<String>();
				gs.add(g.getName());
				nng.put(nname, gs);
			}
		}
		log.debug("update node&group table: " + nng);
		//remove myself
		nnames.remove(session.getContext().getMeNode().getName());
		log.debug(nnames + "in spath select");
		Collections.sort(nnames, new Comparator<String>() {

			public int compare(String o1, String o2) {
				if(distance.get(o1) < distance.get(o2))
					return -1;
				else return 1;
			}
		});
		spath.put(g.getName(), nnames.get(0));
		log.debug("update spath table: " + spath);

		// add map gname : expect id
		@SuppressWarnings("unchecked")
		Map<String, Integer> gids = (Map<String, Integer>) session.getContext()
				.getAttribute(Global.prefix+"gids");
//		init has created one, so there can't be without one
//		if (gids == null) {
//			gids = new HashMap<String, Integer>();
//			session.getContext().setAttribute(Global.prefix+"gids", gids);
//		}
		gids.put(g.getName(), 0);
		log.debug("update gids:" + gids);
	}

}
