package lab1.client;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

import lab1.exception.*;
import lab1.model.*;
import lab1.msg.*;
import lab1.service.*;
import lab1.service.impl.*;

/**
 * ClientCRUDService should implement CRUDService. The difference btw other
 * implementations is that the core functionality is implemented on the server
 * send. Client and server communicates through message.
 * 
 * @author amixyue
 * 
 */
public class ClientCRUDService implements CRUDService {

	@Override
	public void loadAutomotives(String XMLfile) throws LoaderException,
			AutomotiveException {
		ArrayList<Automotive> autoList = new ArrayList<Automotive>();
		Loader.loadXML(XMLfile, autoList);
		AddAutoMsg msg = null;
		for (Automotive auto : autoList) {
			// add to server side
			msg = new AddAutoMsg(auto);
			try {
				Logger.log("send auto " + auto.getName());
				Client.send(msg);
			} catch (SocketException e) {
				throw new AutomotiveException("add an automotive error");
			}
		}
	}

	@Override
	public void listAutomotives() throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);

	}

	@Override
	public Automotive findAutomotive(String name) throws AutomotiveException {
		ReadAutoMsg msg = new ReadAutoMsg(name);
		ReadAutoReplyMsg replyMsg = null;
		Automotive auto = null;
		try {
			Logger.log("find auto " + name);
			replyMsg = (ReadAutoReplyMsg) Client.send(msg);
		} catch (SocketException e) {
			throw new AutomotiveException("add an automotive error");
		}

		if (replyMsg != null) {
			if ((auto = (Automotive) replyMsg.getData()) == null) {
				throw new AutomotiveException(
						AutomotiveException.ErrMsg.NO_SUCH);
			} else {
				return auto;
			}
		}
		// actually it can never reach here!
		return null;
	}

	@Override
	public void updateAutomotive(Automotive auto) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);

	}

	@Override
	public Automotive deleteAutomotive(String name) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);
	}

	@Override
	public String listOptions(Automotive auto) {
		Iterator<String> strs = auto.getOptionSetNamesIterator();
		StringBuilder sb = new StringBuilder();
		sb.append("options: ");
		while (strs.hasNext()) {
			sb.append(strs.next() + ";");
		}
		return sb.toString();
	}

	@Override
	public void configure(Automotive auto, String setName, String optionName)
			throws OptionException, AutomotiveException {
		auto.setOptionChoice(setName, optionName);
	}

	@Override
	public int getPrice(Automotive auto) throws OptionException {
		return auto.getPrice();
	}

	@Override
	public String printCar(Automotive car) throws AutomotiveException {
		return car.print(Automotive.PRINT_CAR);
	}
}
