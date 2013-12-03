package lab1.server;

import java.net.Socket;
import java.io.*;

import lab1.model.*;
import lab1.msg.*;
import lab1.service.impl.Logger;

/**
 * This handles a connection from client
 * 
 * @author amixyue
 * 
 */
public class ClientHandler implements Runnable {
	private Socket sock = null;
	private Thread myThread = null;

	public ClientHandler(Socket sock) throws IOException {
		this.sock = sock;
		this.myThread = new Thread(this);
	}

	public void start() {
		myThread.start();
	}

	/**
	 * simply make it a synchronized communication
	 * process data and reply with data or ok.
	 */
	public void run() {
		Automotive auto = null;
		Message msg = null;
		Message.MessageType type = null;
		try {
			try (ObjectInputStream inputStream = new ObjectInputStream(
					this.sock.getInputStream());
					ObjectOutputStream outputStream = new ObjectOutputStream(
							this.sock.getOutputStream())) {
				msg = (Message) inputStream.readObject();
				type = msg.getType();
				Logger.log("server get msg " + type);
				switch (type) {
				case ADD_AUTOMOTIVE:
					auto = (Automotive) (((AddAutoMsg) msg).getData());
					ServerCRUDService.getService().addAutomotive(auto);
					// reply client with OK
					msg = new OKMsg();
					outputStream.writeObject(msg);
					break;
				case READ_AUTOMOTIVE:
					String name = (String) ((ReadAutoMsg) msg).getData();
					auto = ServerCRUDService.getService().findAutomotive(name);
					// reply client with an automotive
					msg = new ReadAutoReplyMsg(auto);
					outputStream.writeObject(msg);
					break;
				default:
					// never mind
					break;
				}
			} finally {
				/* the input stream will be taken care by JVM 
				 * close the socket since we are done
				 * */
				if (this.sock != null) {
					this.sock.close();
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}