package lab1.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import lab1.service.Configure;
import lab1.service.impl.Logger;

/**
 * This models a server and appplies Singleton design pattern.
 * 
 * @author amixyue
 * 
 */
public class Server {
	private static Server server;

	private ServerSocket serverSock;
	private Socket sock;

	public static Server getServer(int serverPort) {
		if (server == null) {
			server = new Server(serverPort);
		}
		return server;
	}

	private Server(int serverPort) {
		try {
			serverSock = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.err.println("create server error at port: " + serverPort);
		}
	}

	/**
	 * infinite loop waiting for connections.
	 */
	public void waitForConnections() {
		while (true) {
			try {
				sock = serverSock.accept();
				ClientHandler handler = new ClientHandler(sock);
				handler.start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static void main(String argv[]) {
		Server server = Server.getServer(Configure.port);
		if (server != null) {
			Logger.log("server waiting on " + Configure.port);
			server.waitForConnections();
		}
	}
}
