package lab.amixyue.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import lab.amixyue.clock.LogicClock;
import lab.amixyue.clock.VectorClock;
import lab.amixyue.constant.Action;
import lab.amixyue.constant.ClockType;
import lab.amixyue.constant.GStatus;
import lab.amixyue.constant.Global;
import lab.amixyue.constant.MessageType;
import lab.amixyue.constant.Protocal;
import lab.amixyue.model.*;
import lab.amixyue.receive.RecvCtrlFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Build context using local file. The build process should be synchronized.
 * 
 * @author amy
 * 
 */
@Log4j
public class LocalContext extends AbstractContext {

	public static Logger logger = Logger.getLogger(LocalContext.class);

	@Setter
	@Getter
	private String path;

	public LocalContext(String path, String name) {
		this.path = path;
		super.name = name;
	}

	public synchronized void buildContext() {

		try {
			writeinit.lock();

			Yaml yaml = new Yaml();
			Iterable<Object> objs = null;

			/*
			 * if my ip is not identical to my ip from configFile then switch
			 * chgConfig to true and construct chgStr, later uploads new
			 * configFile.
			 */
			boolean chgConfig = false;
			String chgStr = "";
			try {
				objs = yaml.loadAll(new FileInputStream(new File(path)));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				logger.fatal("ConfigFile Get Error");
				System.exit(1);
			}

			//clear basic first
			clearBasic();
			for (Object obj : objs) {
				@SuppressWarnings("unchecked")
				Map<String, ArrayList<Map<String, Object>>> map = (Map<String, ArrayList<Map<String, Object>>>) obj;
				for (String key : map.keySet()) {
					ArrayList<Map<String, Object>> objList = map.get(key);

					if (key.equals("ClockService")) {
						logger.debug("parse ClockService");
						for (Map<String, Object> objNode : objList) {
							String tmpTypeStr = (String) objNode.get("type");
							ClockType tmpType = null;
							if(tmpTypeStr != null)
								tmpType = ClockType.valueOf(tmpTypeStr);
							
							if (tmpType != null) {
								if (Context.clock != null
										&& Context.clock.getType()
												.equals(tmpType)) {
									// do nothing
								} else {
									// init clockservice
									log.debug("init clock service: " + tmpType);
									switch(tmpType){
									case logic:
										Context.clock = LogicClock.factory.getClock(this);
										break;
									case vector:
										Context.clock = VectorClock.factory.getClock(this);
										break;
									}
									//clear msg
									this.clearMsgs();
								}
							}
						}
					} else if (key.equals("Configuration")) {
						logger.debug("parse Configuration");
						int tmpIndex = 0;
						// TODO: nodes count changes
						for (Map<String, Object> objNode : objList) {
							// check me
							if (name.equals((String) objNode.get("Name"))) {
								Context.meIndex = tmpIndex;
								try {
									if (!((String) objNode.get("IP"))
											.equals(InetAddress.getLocalHost()
													.getHostAddress())) {
										// mark!
										log.debug("ip changed!");
										chgConfig = true;
										objNode.put("IP", InetAddress
												.getLocalHost()
												.getHostAddress());
									}
								} catch (UnknownHostException e) {
									e.printStackTrace();
									logger.fatal("Local IP Address Get Error");
									System.exit(1);
								}
							}

							String proStr = (String) objNode.get("Protocal");
							Protocal protocal = null;
							if(proStr != null)
								protocal = Protocal.valueOf(proStr);
							if (protocal == null) {
								protocal = Protocal.tcp;
							}
							Node tmpNode = new Node(
									(String) objNode.get("Name"),
									(String) objNode.get("IP"),
									(Integer) objNode.get("Port"), protocal);
							Context.nodes.add(tmpNode);

							if (tmpNode.getName().equals(name)) {
								//after node has been initialized
								Context.meNode.copyOf(tmpNode);
								// if protocal changes or no protocal
								if (Context.protocal==null
										|| !Context.protocal
												.equals(protocal)) {
									log.debug("init protocal: " + protocal);
									Context.protocal = protocal;
									RecvCtrlFactory.startRecvCtrl(this, protocal);
								}
							}
							tmpIndex++;
						}
					} else if (key.equals("SendRules")) {
						logger.debug("parse SendRules");
						for (Map<String, Object> objRule : objList) {
							String strKind = (String) objRule.get("Kind");
							MessageType tmpMType = null;
							if(strKind != null)
								tmpMType = MessageType.valueOf(strKind);
							String strAction = (String) objRule.get("Action");
							Action tmpAction = null;
							if(strAction != null)						
								tmpAction = Action.valueOf(strAction);							
							Rule tmpRule = new Rule(
									(String) objRule.get("Src"),
									(String) objRule.get("Dest"), tmpMType,
									tmpAction, (Integer) objRule.get("ID"),
									(Integer) objRule.get("Nth"));
							Context.sendRules.add(tmpRule);
						}
					} else if (key.equals("ReceiveRules")) {
						logger.debug("parse ReceiveRules");
						for (Map<String, Object> objRule : objList) {
							String strKind = (String) objRule.get("Kind");
							MessageType tmpMType = null;
							if(strKind != null)
								tmpMType = MessageType.valueOf(strKind);
							String strAction = (String) objRule.get("Action");
							Action tmpAction = null;
							if(strAction != null)						
								tmpAction = Action.valueOf(strAction);	
							Rule tmpRule = new Rule(
									(String) objRule.get("Src"),
									(String) objRule.get("Dest"), tmpMType,
									tmpAction, (Integer) objRule.get("ID"),
									(Integer) objRule.get("Nth"));
							Context.recvRules.add(tmpRule);
						}
						// uploads configFile
						if (chgConfig) {
							if(Context.clock!=null){
								chgStr += "\nClockService :";
								chgStr += "\n- type : " + Context.clock.getType();
							}
							if(Context.nodes.size()>0){
								chgStr += "\nConfiguration :";
								for (Node node : Context.nodes) {
									chgStr += node.configStr();
								}
							}	
							if(Context.sendRules.size()>0){
								chgStr += "\nSendRules :";
								for(Rule rule : Context.sendRules){
									chgStr += rule.configStr();
								}
							}	
							if(Context.recvRules.size()>0){
								chgStr += "\nReceiveRules :";
								for(Rule rule :Context.recvRules){
									chgStr += rule.configStr();
								}
							}
							FileWriter fw = null;
							try {
								fw = new FileWriter(new File(path));
								fw.write(chgStr);
								fw.close();
								logger.debug(path + " Writed");
							} catch (IOException e) {
								e.printStackTrace();
								logger.fatal(path + "Write Error");
							}

						}
					}
				}
			}
			
			//add a group for broadcast
			Group broadgroup = new Group("broad", Context.nodes, GStatus.idle);
			this.setAttribute(Global.prefix+"broad", broadgroup);
			
			//add vote
			this.setAttribute(Global.prefix+"vote", false);
			
			//build hashmap distance nname:int
			final Map<String, Integer> distance = new HashMap<String, Integer>();
			HashSet<String> snames = new HashSet<String>();
			ArrayList<String> anames = new ArrayList<String>();
			
			for(Node n : Context.nodes){
				distance.put(n.getName(), new Random().nextInt(10));
				snames.add(n.getName());
				anames.add(n.getName());
			}
			this.setAttribute(Global.prefix+"distance", distance);
			log.debug("add distance table: " + distance);		
			
			
			// add map 
			// if there exists one, it should be there until cleared
			// just keep the unexisted nodes
			@SuppressWarnings("unchecked")
			Map<String, HashSet<String>> nng = (HashMap<String, HashSet<String>>) this
					.getAttribute(Global.prefix+"nng");
			
			@SuppressWarnings("unchecked")
			Map<String, String> spath = (HashMap<String, String>) this
					.getAttribute(Global.prefix+"spath");
			
			@SuppressWarnings("unchecked")
			Map<String, Integer> gids = (Map<String, Integer>) this
					.getAttribute(Global.prefix+"gids");
			
			if (nng == null) {
				nng = new HashMap<String, HashSet<String>>();
				this.setAttribute(Global.prefix+"nng", nng);
				log.debug("create node&group table.");
			}

			if (spath == null) {
				spath = new HashMap<String, String>();
				this.setAttribute(Global.prefix+"spath", spath);
				log.debug("create group&spath table.");
			}
			
			if (gids == null) {
				gids = new HashMap<String, Integer>();
				this.setAttribute(Global.prefix+"gids", gids);
				log.debug("create group&expectid table.");
			}
//			Collections.sort(anames, new Comparator<String>() {
//
//				public int compare(String o1, String o2) {
//					if(distance.get(o1) < distance.get(o2))
//						return -1;
//					else return 1;
//				}
//			});
			gids.put(Global.prefix+"broad", 0);
			//subgroup
			//nng.put(Global.prefix+"broad",snames );
//			spath.put(Global.prefix+"broad",anames.get(0));
		} finally {
			writeinit.unlock();
		}
		//log.debug("buildContext: ");
	}

}
