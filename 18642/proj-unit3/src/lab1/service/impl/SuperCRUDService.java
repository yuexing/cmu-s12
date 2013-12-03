package lab1.service.impl;

import java.util.ArrayList;

import lab1.exception.AutomotiveException;
import lab1.exception.LoaderException;
import lab1.model.Automotive;

public class SuperCRUDService extends AdminCRUDService {

	/**
	 * A super user can load all the autos.
	 */
	@Override
	public void loadAutomotives(String XMLfile)
			throws LoaderException, AutomotiveException {		 
		ArrayList<Automotive> autoList = new ArrayList<Automotive>();
		Loader.loadXML(XMLfile, autoList);
		for(Automotive auto : autoList){
				autos.put(auto.getName(), auto);
		}
	}

}
