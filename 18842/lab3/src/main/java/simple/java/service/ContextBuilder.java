package simple.java.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.yaml.snakeyaml.Yaml;

import simple.java.clock.Clock.ClockType;
import simple.java.manager.ClockManager;
import simple.java.manager.GroupManager;
import simple.java.manager.NodeManager;
import simple.java.manager.RuleManager;
import simple.java.model.Group;
import simple.java.model.Message.MessageType;
import simple.java.model.Node;
import simple.java.model.Rule;
import simple.java.model.Rule.Action;

@Log4j
public class ContextBuilder {

	private static String path;
	private static String name;

	public static void rebuild() {
		log.debug("reload file");
		build(path, name);
	}

	public static void build(String path, String name) {
		ContextBuilder.path = path;
		ContextBuilder.name = name;

		boolean ipchanged = false;

		Yaml yaml = new Yaml();
		Iterable<Object> objs = null;
		// InputStream is = ClassLoader.getSystemResourceAsStream(path);
		InputStream fi = null;
		try {
			fi = new FileInputStream(path);
			objs = yaml.loadAll(fi);
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}

		// objs = yaml.loadAll(new FileInputStream(new File(path)));
		ClockType clockType = null;
		for (Object obj : objs) {
			@SuppressWarnings("unchecked")
			Map<String, ArrayList<Map<String, Object>>> map = (Map<String, ArrayList<Map<String, Object>>>) obj;
			for (String key : map.keySet()) {
				ArrayList<Map<String, Object>> objList = map.get(key);

				if (key.equals("ClockService")) {
					log.debug("parse ClockService");
					for (Map<String, Object> objNode : objList) {
						String tmpTypeStr = (String) objNode.get("type");
						
						if (tmpTypeStr != null)
							clockType = ClockType.valueOf(tmpTypeStr);
						
					}
				} else if (key.equals("Configuration")) {
					log.debug("parse Configuration");
					NodeManager.clear();
					
					//for group;
					ArrayList<String> group = null;
					String groupName  = null;
					
					for (Map<String, Object> objNode : objList) {
						// check ip
						if (name.equals((String) objNode.get("Name"))) {
							String currentIp;
							try {
								currentIp = InetAddress.getLocalHost()
										.getHostAddress();
								if (!currentIp.equals((String) objNode
										.get("IP"))) {
									objNode.put("IP", currentIp);
									ipchanged = true;
								}
							} catch (UnknownHostException e) {
								e.printStackTrace();
							}

							//@SuppressWarnings("unchecked")
							group = (ArrayList<String>) objNode.get("Group");
							
							if(group != null){
								groupName = group.get(0);
								String[] nodes = new String[group.size() -1 ];
								for(int i = 1; i < group.size(); i++){
									nodes[i-1] = group.get(i);
								}
								Group g = new Group(groupName, nodes);
								GroupManager.addGroup(g);
							}
							
							
						}
						Node tmpNode = new Node((String) objNode.get("Name"),
								(String) objNode.get("IP"),
								(Integer) objNode.get("Port"));
						if(group != null){
							tmpNode.setGname(groupName);
						}
						NodeManager.addNode(tmpNode);				
					
					}
				} else if (key.equals("SendRules")) {
					log.debug("parse SendRules");
					RuleManager.clearSendRule();
					for (Map<String, Object> objRule : objList) {
						String strKind = (String) objRule.get("Kind");
						MessageType tmpMType = null;
						if (strKind != null)
							tmpMType = MessageType.valueOf(strKind);
						String strAction = (String) objRule.get("Action");
						Action tmpAction = null;
						if (strAction != null)
							tmpAction = Action.valueOf(strAction);

						Rule tmpRule = new Rule((String) objRule.get("Src"),
								(String) objRule.get("Dest"), tmpMType,
								tmpAction, (Integer) objRule.get("ID"),
								(Integer) objRule.get("Nth"));
						RuleManager.addSendRule(tmpRule);
					}
				} else if (key.equals("ReceiveRules")) {
					log.debug("parse ReceiveRules");
					RuleManager.clearRecvRule();
					for (Map<String, Object> objRule : objList) {
						String strKind = (String) objRule.get("Kind");
						MessageType tmpMType = null;
						if (strKind != null)
							tmpMType = MessageType.valueOf(strKind);
						String strAction = (String) objRule.get("Action");
						Action tmpAction = null;
						if (strAction != null)
							tmpAction = Action.valueOf(strAction);
						Rule tmpRule = new Rule((String) objRule.get("Src"),
								(String) objRule.get("Dest"), tmpMType,
								tmpAction, (Integer) objRule.get("ID"),
								(Integer) objRule.get("Nth"));
						RuleManager.addRecvRule(tmpRule);
					}
				}
			}
		}
		
		/**
		 * change clock type here
		 */
		if (clockType != null && ClockManager.type != clockType) {
			ClockManager.init(clockType);
		}
		// write file
//		if (ipchanged) {
//
//			String chgStr = "";
//			if (ClockManager.clock != null) {
//				chgStr += "\nClockService :";
//				chgStr += "\n- type : " + ClockManager.type;
//			}
//			if (NodeManager.count() > 0) {
//				chgStr += "\nConfiguration :";
//				for (Node node : NodeManager.getNodes()) {
//					chgStr += node.configStr();
//				}
//			}
//			if (RuleManager.countRecv() > 0) {
//				chgStr += "\nReceiveRules :";
//				for (Rule rule : RuleManager.getRecvRules()) {
//					chgStr += rule.configStr();
//				}
//			}
//			if (RuleManager.countSend() > 0) {
//				chgStr += "\nSendRules :";
//				for (Rule rule : RuleManager.getSendRules()) {
//					chgStr += rule.configStr();
//				}
//			}
//
//			File nfile = new File(path);
//			FileOutputStream fos;
//			try {
//				fos = new FileOutputStream(nfile);
//				fos.write(chgStr.getBytes());
//				fos.flush();
//				fos.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			log.debug("reload file! and ip is " + NodeManager.getNode(name).ip);
//		}
	}
}
