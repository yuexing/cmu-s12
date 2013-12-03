package simple.java.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.extern.log4j.Log4j;

import simple.java.manager.NodeManager;
import simple.java.model.LogicTimedMessage;
import simple.java.model.Message;
import simple.java.model.Node;
import simple.java.model.VectorTimedMessage;
@Log4j
public class Sender {

	public static void send(Message msg){
		String name = msg.getDest();
		Node dest = NodeManager.getNode(name);
		try {
			if(msg instanceof VectorTimedMessage){
				log.debug("send: " + dest.name + ", " 
						+ dest.ip + ": " + dest.port + " msg: " + (VectorTimedMessage)msg);
			}else if(msg instanceof LogicTimedMessage){
				log.debug("send: " + dest.name + ", " 
						+ dest.ip + ": " + dest.port + " msg: " + (LogicTimedMessage)msg);
			}
			
			Socket socket = new Socket(dest.ip, dest.port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(msg);
			oos.close();
			socket.close();
		} catch (UnknownHostException e) {
			log.debug(name + " address error!");
		} catch (IOException e) {
			log.debug(name + " connect error!");
		}
	}
}
