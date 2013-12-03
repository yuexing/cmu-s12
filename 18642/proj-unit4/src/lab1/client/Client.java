package lab1.client;

import java.io.*;
import java.net.*;

import lab1.msg.*;
import lab1.service.Configure;
import lab1.service.impl.Logger;

/**
 * This encapsulate the functionality of a client node, which is simply to send 
 * a message and receive a reply from the server.
 * @author amixyue
 *
 */
public class Client {

	public static Message send(Message msg) throws SocketException {
		Message replyMsg = null;

		try {
			InetAddress addr = InetAddress.getByName(Configure.name);
			int port = Configure.port;
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket sock = new Socket();

			int timeoutMs = 2000; // connect with timeout: 2 seconds
			sock.connect(sockaddr, timeoutMs);
			ObjectOutputStream outputStream  = null;
			ObjectInputStream inputStream = null;

			try {
				outputStream = new ObjectOutputStream(
						sock.getOutputStream());
				Logger.log("write obj");
				outputStream.writeObject(msg);

				inputStream = new ObjectInputStream(
						sock.getInputStream());
				Logger.log("read obj");
				replyMsg = (Message) inputStream.readObject();
			} finally {
				if(inputStream != null){
					inputStream.close();
				}
				if(outputStream != null){
					outputStream.close();
				}
				if (sock != null) {
					sock.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// throw new SocketException("Read and write to client error");
		}
		return replyMsg;
	}
}
