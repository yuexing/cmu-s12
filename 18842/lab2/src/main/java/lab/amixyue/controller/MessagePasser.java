package lab.amixyue.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import lab.amixyue.constant.*;
import lab.amixyue.context.*;
import lab.amixyue.model.*;
import lab.amixyue.pipeline.*;
import lab.amixyue.receive.*;
import lombok.extern.log4j.Log4j;

/**
 * MessagePasser is only responsible for send and receive action. It needs a
 * context for nodes and send/receive queue or others. To init a MessagePasser,
 * we have different selections including remote file on ftp or url, local file,
 * or a map in memory.
 * <p>
 * While sending a {@link Message}, {@link MessagePasser} ask
 * {@link SendRuleController} first, which may include update send/delay queue.
 * Once the current {@link Message} is delayed, that means no {@link Message}
 * should be sent out.
 * <p>
 * After checking rules, if no delay, {@link SendController} will send delay
 * queue first(if not empty), and then the send queue.
 * <p>
 * While receiving a {@link Message},{@link MessagePasser} will get msg first,
 * and call {@link RecvRuleController}.
 * <p>
 * Those who can modify context include {@link FileTask}, {@link RecvController}, {@link MessagePasser}.receive() and {@link RecvRuleController},
 * {@link SendRuleController} called by {@link MessagePasser}.send(msg). how to
 * synchronize context. how to share(synchronize) a context btw nodes.
 * 
 * TODO: mutual control efficient socket
 * 
 * @author amy
 * @date Feb 3 2012
 */
@Log4j
public class MessagePasser {

	AbstractContext contextBuilder;
	FileTask task;
	FileSchedual fileSchedual;
	PipelineChooser pipelineChooser;

	public MessagePasser(ConfigPathType type, String path, String name) {
		/**
		 * if there should exist a builder chooser
		 */
		switch (type) {
		case url:
			contextBuilder = new URLContext(path, name);
			break;
		case local:
			contextBuilder = new LocalContext(path, name);
			break;
		case ftp:
			contextBuilder = new FTPContext(path, name);
			break;
		case clazzpath:
			contextBuilder = new ClazzPathContext(name);
			break;
		case map:
			// for test
			HashMap<String, ArrayList<HashMap<String, Object>>> map = null;
			contextBuilder = new MapContext(map);
			break;
		default:
			// local error
			System.exit(1);
			break;
		}
		contextBuilder.buildContext();
		fileSchedual = new FileSchedual(path, contextBuilder);
		fileSchedual.start();
		pipelineChooser = new PipelineChooserImpl(contextBuilder);
	}

	/**
	 * While sending a {@link Message}, {@link MessagePasser} ask
	 * {@link SendRuleController} first, which may include update send/delay
	 * queue. Once the current {@link Message} is delayed, that means no
	 * {@link Message} should be sent out.
	 * <p>
	 * After checking rules, if no delay, {@link SendController} will send delay
	 * queue first(if not empty), and then the send queue.
	 * 
	 * @param msg
	 */
	public void send(Message msg) {
		
			log.debug("begin to send: " + msg);
			contextBuilder.setAttribute("!@#$%^&*sendMsg", msg);
			switch (msg.getType()) {
			case group:
				pipelineChooser.choose(PipelineType.group);
				break;
			case gstart:
				pipelineChooser.choose(PipelineType.gstart);
				break;
			case gend:
				pipelineChooser.choose(PipelineType.gend);
				break;
			default:
				pipelineChooser.choose(PipelineType.send);
			}
		

	}

	/**
	 * While receiving a {@link Message},{@link MessagePasser} will get msg
	 * first, and call {@link RecvRuleController}.
	 */
	public Message receive(MessageType multi) {
//		if (multi != null && multi.equals(MessageType.multi)) {
//			pipelineChooser.choose(PipelineType.multirecv);
//			return null;
//		}
		pipelineChooser.choose(PipelineType.receive);
		Message msg = (Message) contextBuilder.getAttribute(Global.prefix + "recvMsg");
		contextBuilder.setAttribute(Global.prefix + "recvMsg", null);
		if (msg != null)
			log.debug("recieved: " + msg);
		else
			log.debug("No messages in the buffer");
		return msg;
	}

	/**
	 * UI or command line ask for name to new a Message
	 * 
	 * @return
	 */
	public String getName() {
		return contextBuilder.getName();
	}

	public static void main(String[] args) {
		MessagePasser mp = new MessagePasser(ConfigPathType.local,
				"D:\\lab0.config", "A");
		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out
						.println(mp.getName() + " Please Enter the Command: ");
				String command = wt.readLine();
				if (command.equals("end"))
					break;
				// command 1: sendto [name] [kind] [content]
				// command 2: receive
				// command 3: group [gname] [node1] [node2]...
				// command 4: gstart [gname]
				// command 5: gend [gname]
				if (command.startsWith("receive")) {
					String[] vars = command.split("\\s+");
					
						mp.receive(null);
					

				} else if (command.startsWith("g")) {
					if (command.startsWith("gstart")) {
						String[] vars = command.split("\\s+");
						if (!vars[0].equals("gstart") || vars.length != 2) {
							log.error("Error Command");
							continue;
						}
						String gname = vars[1];
						Message msg = null;
						try {
							msg = new Message(mp.getName(), gname,
									MessageType.gstart, null);
						} catch (IllegalArgumentException e) {
							continue;
						}
						mp.send(msg);
					} else if (command.startsWith("gend")) {
						String[] vars = command.split("\\s+");
						if (!vars[0].equals("gend") || vars.length != 2) {
							log.error("Error Command");
							continue;
						}
						String gname = vars[1];
						Message msg = null;
						try {
							msg = new Message(mp.getName(), gname,
									MessageType.gend, null);
						} catch (IllegalArgumentException e) {
							continue;
						}
						mp.send(msg);
					} else {
						String[] vars = command.split("\\s+");
						if (!vars[0].equals("group") || vars.length < 3) {
							log.error("Error Command");
							continue;
						} else {
							String gname = vars[1];
							String[] nodes = new String[vars.length - 2];
							for (int i = 2; i < vars.length; i++) {
								nodes[i - 2] = vars[i];
							}
							Message msg = null;
							try {
								msg = new Message(mp.getName(), gname,
										MessageType.group, nodes);
							} catch (IllegalArgumentException e) {
								continue;
							}
							mp.send(msg);
						}
					}
				} else {
					//sendto gname write data
					//for ME lab3
					if (!command.startsWith("sendto")) {
						log.error("Error Command");
						continue;
					} else {
						String[] vars = command.split("\\s+");
						if (vars.length != 4) {
							log.error("Error Command");
							continue;
						} else {
							Message msg = null;
							try {
								msg = new Message(mp.getName(), vars[1],
										MessageType.valueOf(vars[2]), vars[3]);
							} catch (IllegalArgumentException e) {
								continue;
							}
							mp.send(msg);
							// TODO: send to logger
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
