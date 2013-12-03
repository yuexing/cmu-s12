package simple.java.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import lombok.extern.log4j.Log4j;

import simple.java.manager.BufferManager;
import simple.java.manager.FileManager;
import simple.java.manager.RuleManager;
import simple.java.model.LogicTimedMessage;
import simple.java.model.Message;
import simple.java.model.Rule.Action;
import simple.java.model.VectorTimedMessage;

@Log4j
public class Connector implements Runnable{

	private Socket socket;
	
	public Connector(Socket socket){
		this.socket = socket;
	}

	public void run() {
		/**
		 * make a transaction here
		 */
//		if (FileManager.checkChange()) {
//			ContextBuilder.rebuild();
//		}
		
		boolean isGroupMsg = false;
		
		ObjectInputStream ois;
		Message msg  = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			msg = (Message) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		
		if(msg.getGname() != null){
			isGroupMsg = true;
		}
		//action
		Action action = RuleManager.checkRecv(msg);
		
		switch (action) {
		case drop:
			//
			if(msg instanceof VectorTimedMessage){
				log.debug("drop: " + (VectorTimedMessage)msg);
			}else if(msg instanceof LogicTimedMessage){
				log.debug("drop: " + (LogicTimedMessage)msg);
			}
			break;
		case delay:
			if(msg instanceof VectorTimedMessage){
				log.debug("delay: " + (VectorTimedMessage)msg);
			}else if(msg instanceof LogicTimedMessage){
				log.debug("delay: " + (LogicTimedMessage)msg);
			}
			BufferManager.addRecvDelay(msg);
			break;
		case duplicate:
			if(msg instanceof VectorTimedMessage){
				log.debug("duplicate: " + (VectorTimedMessage)msg);
			}else if(msg instanceof LogicTimedMessage){
				log.debug("duplicate: " + (LogicTimedMessage)msg);
			}
			if(isGroupMsg){
				GroupMessageRecvProcessor.process(msg);
				GroupMessageRecvProcessor.process(msg);
				break;
			}
			BufferManager.addRecv(msg);
			BufferManager.addRecv(msg);
			break;
		case none:
			if(msg instanceof VectorTimedMessage){
				log.debug("normal: " + (VectorTimedMessage)msg);
			}else if(msg instanceof LogicTimedMessage){
				log.debug("normal: " + (LogicTimedMessage)msg);
			}
			if(isGroupMsg){
				GroupMessageRecvProcessor.process(msg);
				break;
			}
			BufferManager.addRecv(msg);
			break;
		}
	}

}
