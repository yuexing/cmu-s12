package simple.java.core;

import simple.java.manager.FileManager;
import simple.java.manager.GroupManager;
import simple.java.manager.NodeManager;
import simple.java.model.Group;
import simple.java.model.Group.Status;
import simple.java.model.Message;
import simple.java.model.Message.MessageType;
import simple.java.model.Resource;
import simple.java.service.ContextBuilder;
import simple.java.service.MessagePasser;
import simple.java.service.Receiver;
import simple.java.ui.Command;
import simple.java.ui.Command.CommandType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import lombok.extern.log4j.Log4j;
@Log4j
public class Core {

	public final String meName;
	private MessagePasser mp;
	private Command command;
	private ArrayList<Message> buf;
	
	public Core(String path, String name){
		this.meName = name;
		this.buf = new ArrayList<Message>();
		FileManager.init(path);
		//should go beyond build in order to update the index
		//really not a good way
		NodeManager.setMyname(name);
		ContextBuilder.build(path, name);
		command = new Command(this);
		mp = MessagePasser.getMessagePasser();
		Thread th = new Thread(new Receiver(this));
		th.start();
		command.run();
	}
	
	public void parse(String...vars){
		
		/**
		 * as a transation, check current context and then do
		 * parse -> send 
		 * -> recv
		 * clear problem
		 */
//		if (FileManager.checkChange()) {
//			ContextBuilder.rebuild();
//		}
		
		CommandType type = CommandType.valueOf(vars[0]);
		switch(type){
		case send:
			Message msg = new Message(meName, vars[1], MessageType.valueOf(vars[2]), vars[3]);
			mp.send(msg);
			break;
		case gsend:
			Message gmsg = new Message(meName, vars[1], MessageType.valueOf(vars[2]), Resource.valueOf(vars[3]));
			Group g = GroupManager.getGroupByName(vars[1]);
			if(g == null){
				log.debug("Group not exist!");
				return;
			}
			gmsg.setGname(vars[1]);
			gmsg.setAck(false);
			gmsg.setSendNo(g.getSendNo());
			g.addSendNo();
			gmsg.setDeliverNo(g.getDeliverNoByName(gmsg.src));
			if(MessageType.valueOf(vars[2]).equals(MessageType.request)){
				gmsg.setRequestNo(g.getRequestNo());
				g.addRequestNo();
				g.setRequestAck(g.getRequestNo());
				g.addBackupRequestMsg(gmsg);
			}
			g.setStatus(Status.Wanted);
			mp.send(gmsg);
			break;
		case recv:
			buf = new ArrayList<Message>(mp.recv());
			if(buf.size() == 0)
				command.showMsg("No message Recved");
			else 
				command.showMsg(buf);
			break;
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Enter Your Name, Please:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name = null;
		try {
			name = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		Core core = new Core("lab.config", name);		
	}

}
