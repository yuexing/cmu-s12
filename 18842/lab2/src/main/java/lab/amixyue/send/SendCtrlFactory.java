package lab.amixyue.send;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import lab.amixyue.constant.Protocal;
import lab.amixyue.model.Message;
import lombok.extern.log4j.Log4j;

@Log4j
public class SendCtrlFactory {

	public static SendController getSendCtrl(Protocal type){
		SendController instance = null;
		switch(type){
		case tcp:
			instance = new TCPSendController();
			break;
		case udp:
			instance = new UDPSendController();
			break;
		}
		return instance;
	}
	
	static class TCPSendController extends SendController{

		private static final int TIMEOUT = 10000;
		
		@Override
		public boolean send(String ip, int port, Object data) {
			boolean sent = true;
			log.debug("sending to: " + ip + ": " + port + (Message)data);
			Socket socket = new Socket();
			ObjectOutputStream oos = null;
			try {
				socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), TIMEOUT);
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(data);
				log.debug("send by tcp success");
				oos.close();
				socket.close();
			} catch (UnknownHostException e) {
				sent = false;
				log.error("dest not find!");
			} catch (IOException e) {
				sent = false;
				log.error("out put or connect error!");
			}
			return sent;
		}

	}
	
	static class UDPSendController extends SendController{
		@Override
		public boolean send(String ip, int port, Object data) {
			log.debug("sending udp");
			return false;
		}
	}

}
