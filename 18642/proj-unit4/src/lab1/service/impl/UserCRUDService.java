package lab1.service.impl;

import java.util.*;

import lab1.exception.AutomotiveException;
import lab1.exception.LoaderException;
import lab1.exception.OptionException;
import lab1.model.Automotive;

/**
 * An implementation of crud for common user. The user can only operate on a
 * car or list the existing automotives.
 * 
 * @author amixyue
 * 
 */
public class UserCRUDService extends AbstractCRUDService {

	public UserCRUDService(ArrayList<Automotive> autos) {
		for (Automotive auto : autos) {
			this.autos.put(auto.getName(), auto);
		}
	}
	

	@Override
	public void listAutomotives() {
		for (Automotive auto : this.autos.values()) {
			System.out.println(auto);
		}
	}

	@Override
	public void loadAutomotives(String XMLfile) throws LoaderException,
			AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NO_PERMIT);
	}

	@Override
	public void updateAutomotive(Automotive auto) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NO_PERMIT);
	}

	@Override
	public Automotive deleteAutomotive(String name) throws AutomotiveException {
		throw new AutomotiveException(AutomotiveException.ErrMsg.NO_PERMIT);
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
