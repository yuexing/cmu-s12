package simple.java.service;

import java.io.IOException;
import java.net.*;

import lombok.extern.log4j.Log4j;

import simple.java.core.Core;
import simple.java.manager.NodeManager;
@Log4j
public class Receiver implements Runnable {

//	private Core core;
	private String name;
	
	public Receiver(Core core){
//		this.core = core;
		this.name = core.meName;
	}
	
	public void run() {
		try {
			int port = NodeManager.getNode(name).port;
			log.debug("recv at: " + port);
			ServerSocket server = new ServerSocket(port);
			while(true){
				Socket socket = server.accept();
				new Thread(new Connector(socket)).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
