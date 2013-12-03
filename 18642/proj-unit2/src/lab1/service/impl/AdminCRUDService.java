package lab1.service.impl;

import java.util.*;

import lab1.exception.AutomotiveException;
import lab1.exception.LoaderException;
import lab1.model.Automotive;

/**
 * An implementation of crud for an administrator. An administrator can only
 * operate on automotives of the coresponding maker.
 * 
 * @author amixyue
 * 
 */
public class AdminCRUDService extends AbstractCRUDService {

	private String maker;

	public AdminCRUDService() {
		/* this is for the convienience of SuperCRUDService */
	}

	public AdminCRUDService(String maker) {
		this.maker = maker;
	}

	/**
	 * An admin can only load automotives from a maker.
	 */
	@Override
	public void loadAutomotives(String XMLfile) throws LoaderException,
			AutomotiveException {
		ArrayList<Automotive> autoList = new ArrayList<Automotive>();
		Loader.loadXML(XMLfile, autoList);
		for (Automotive auto : autoList) {
			if (!auto.getMaker().equals(maker)) {
				throw new AutomotiveException(
						AutomotiveException.ErrMsg.NO_PERMIT);
			} else {
				autos.put(auto.getName(), auto);
			}
		}
	}

	@Override
	public void updateAutomotive(Automotive auto) throws AutomotiveException {
		if ((auto = autos.get(auto.getName())) == null) {
			throw new AutomotiveException(AutomotiveException.ErrMsg.NO_SUCH);
		} else {
			this.autos.put(auto.getName(), auto);
		}
	}

	@Override
	public Automotive deleteAutomotive(String name) throws AutomotiveException {
		if (autos.get(name) == null) {
			throw new AutomotiveException(AutomotiveException.ErrMsg.NO_SUCH);
		} else {
			return this.autos.remove(name);
		}
	}

	/**
	 * I do not want to allow an admin to configure a car, since this weaks the
	 * role definition.
	 * 
	 * One login user can be a admin and a user, thus he can do it when he is a
	 * user.
	 */
	@Override
	public void configure(Automotive auto, String setName, String optionName)
			throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.USER_OP);
	}

	@Override
	public int getPrice(Automotive auto) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.USER_OP);
	}

	@Override
	public String printCar(Automotive car) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.USER_OP);
	}

	

}
