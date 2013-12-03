package lab1.server;

import lab1.exception.AutomotiveException;
import lab1.exception.LoaderException;
import lab1.exception.OptionException;
import lab1.model.Automotive;
import lab1.service.impl.AbstractCRUDService;

/**
 * This models the CURD operation on the server side.
 * I extends it will AbstractCRUDService since this will allow
 * further functionality development.
 * 
 * synchronization is needed for this class.
 * 
 * I also make it singleton to be consistent with Server.
 * @author amixyue
 *
 */
public class ServerCRUDService extends AbstractCRUDService {

    private static ServerCRUDService service;
    
    public static ServerCRUDService getService(){
    	if(service == null){
			service = new ServerCRUDService();
    	}
    	return service;
    }
    
	/**
	 * this is the server privileged code
	 * @param auto
	 */
	public synchronized void addAutomotive(Automotive auto) {
		this.autos.put(auto.getName(), auto);
	}
	
	@Override
	public synchronized void loadAutomotives(String XMLfile) throws LoaderException,
			AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);
	}

	@Override
	public synchronized void updateAutomotive(Automotive auto) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);

	}

	@Override
	public synchronized Automotive deleteAutomotive(String name) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);
	}

	@Override
	public synchronized void configure(Automotive auto, String setName, String optionName)
			throws OptionException, AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);

	}

	@Override
	public int getPrice(Automotive auto) throws OptionException,
			AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);
	}

	@Override
	public synchronized String printCar(Automotive car) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NOT_SUP);
	}

}
