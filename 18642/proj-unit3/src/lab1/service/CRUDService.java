package lab1.service;

import lab1.exception.*;
import lab1.model.*;

/**
 * The service for roles. The roles include user, admin, and super user.
 * According to the role, the implementation of services is different.
 * 
 * @author amixyue
 * 
 */
public interface CRUDService {

	/* Automotive */
	/**
	 * load automotives
	 * 
	 * @param XMLfile
	 * @return
	 * @throws LoaderException
	 * @throws AutomotiveException
	 */
	void loadAutomotives(String XMLfile) throws LoaderException,
			AutomotiveException;

	/**
	 * list all automotives
	 */
	void listAutomotives();

	/**
	 * find an automotive by name
	 * 
	 * @param name
	 * @return
	 * @throws AutomotiveException
	 */
	Automotive findAutomotive(String name) throws AutomotiveException;

	/**
	 * update an automotive
	 * 
	 * @param auto
	 * @throws AutomotiveException
	 */
	void updateAutomotive(Automotive auto) throws AutomotiveException;

	/**
	 * delete an automotive
	 * 
	 * @param name
	 * @return
	 * @throws AutomotiveException
	 */
	Automotive deleteAutomotive(String name) throws AutomotiveException;

	/**
	 * list all the options for an automotive
	 * @param auto
	 * @return
	 */
	String listOptions(Automotive auto);

	/* Car */
	/**
	 * configure a car, and this only allows user
	 * 
	 * @param auto
	 * @param setName
	 * @param optionName
	 * @throws OptionException
	 * @throws AutomotiveException
	 */
	void configure(Automotive auto, String setName, String optionName)
			throws OptionException, AutomotiveException;

	/**
	 * get a price for a car, and this only allows user.
	 * 
	 * @param auto
	 * @return
	 * @throws OptionException
	 * @throws AutomotiveException
	 */
	int getPrice(Automotive auto) throws OptionException, AutomotiveException;
	/**
	 * print the user's choice
	 * @param car
	 */
	String printCar(Automotive car) throws AutomotiveException;
}
