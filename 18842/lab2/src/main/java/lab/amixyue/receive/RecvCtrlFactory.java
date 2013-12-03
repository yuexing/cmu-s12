package lab.amixyue.receive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lab.amixyue.constant.MessageType;
import lab.amixyue.constant.PipelineType;
import lab.amixyue.constant.Protocal;
import lab.amixyue.context.Context;
import lab.amixyue.model.*;
import lab.amixyue.pipeline.*;
import lombok.extern.log4j.Log4j;

/**
 * connection efficiency
 * 
 * @author amy
 * 
 */
@Log4j
public class RecvCtrlFactory {

	private static RecvController instance;
	private static Thread recvTh;
	private static Protocal type;

	@SuppressWarnings("deprecation")
	public static void startRecvCtrl(Context context, Protocal type) {
		if (RecvCtrlFactory.type == null || RecvCtrlFactory.type != type
				|| instance == null) {
			RecvCtrlFactory.type = type;
			switch (type) {
			case tcp:
				instance = new TCPRecvController(context);
				break;
			case udp:
				instance = new UDPRecvController(context);
				break;
			}
			if (recvTh != null)
				recvTh.destroy();
			recvTh = new Thread(instance);
			recvTh.start();
		}
	}

	static class TCPRecvController extends RecvController {

		private ServerSocket server;

		public TCPRecvController(Context context) {
			super.context = context;
			super.pipelineChooser = new PipelineChooserImpl(context);

			int port = context.getMeNode().getPort();
			try {
				server = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// should have its own pipeline chooser
		public void run() {
			while (true) {

				try {
					final Socket client = server.accept();
					Thread cTh = new Thread(new Runnable() {
						// all use recv and change to send until session in case
						// of
						// conflict
						public void run() {
							ObjectInputStream ois;
							try {
								ois = new ObjectInputStream(client
										.getInputStream());
								Message m = (Message) ois.readObject();

								if (m.getType().equals(MessageType.group)) {

									// for share code
									context.setAttribute("!@#$%^&*recvfMsg", m);
									pipelineChooser
											.choose(PipelineType.recvGroup);
									log.debug(m);
								} else if (m.getType().equals(MessageType.gend)) {
									try {
										Context.writeinit.lock();
										Group g = (Group) m.getData();
										context.setAttribute(g.getName(), g);
										log.debug(m);
									} finally {
										Context.writeinit.unlock();
									}
								} else if (m.getType().equals(
										MessageType.gstart)) {
									try {
										Context.writeinit.lock();
										Group g = (Group) m.getData();
										context.setAttribute(g.getName(), g);
										log.debug(m);
									} finally {
										Context.writeinit.unlock();
									}
								} else{
									context.setAttribute("!@#$%^&*recvfMsg", m);
									pipelineChooser.choose(PipelineType.recvCtrl);
								}

							} catch (IOException e) {

								e.printStackTrace();
							} catch (ClassNotFoundException e) {

								e.printStackTrace();
							}

						}
					});
					cTh.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	static class UDPRecvController extends RecvController {

		public UDPRecvController(Context context) {
			super.context = context;
		}

		public void run() {

		}

	}
}
