package lab1.test;

import java.util.*;
import java.io.*;

import lab1.exception.*;
import lab1.service.*;
import lab1.service.impl.*;
import lab1.model.*;
import static java.lang.System.*;

public class TestService {

	/* This is a very simple test, but it has been proved sufficient enough to test all 
	 * functionalities implemented in this project. Although it is less satisfactory to 
	 * be user-friendly, you still can follow instructions shown in the screen to finish 
	 * all tests.
	 * 
	 */
	public static void main(String[] args) {
		try {
			boolean quit = false;
			Automotive auto = null;
			CRUDService service = null;
			String input = null;
			String tokens[] = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			out.println("select a role (user / admin/ super), q to quit");

			input = br.readLine();
			switch (input) {
			case "user":
				/*
				 * In the real system, arrayList will be replaced by database.
				 */
				ArrayList<Automotive> autos = new ArrayList<Automotive>();
				Loader.loadXML("automotive.xml", autos);
				service = new UserCRUDService(autos);
				break;
			case "admin":
				out.println("Enter your maker info:");
				input = br.readLine();
				service = new AdminCRUDService(input);
				break;
			case "super":
				service = new SuperCRUDService();
				break;
			case "q":
				quit = true;
				break;
			}
			
			listOptions();
			while (!quit) {
				try {
					input = br.readLine();
					switch (input) {
					case "q":
						return;
					case "l":
						listOptions();
						break;
					case "1":
						out.println("Enter File Name:");
						input = br.readLine();
						service.loadAutomotives(input);
						out.println("Your file has been loaded! Please continue with other options");
						break;
					case "2":
						service.listAutomotives();
						break;
					case "3":
						out.println("Enter Automotive Name:");
						input = br.readLine();
						auto = service.findAutomotive(input);
						if (auto == null) {
							err.println("We can not find the automotive :(");
						} else {
							out.println("Your automotive has been found! Please continue with other options");
						}
						break;
					case "4":
						if (auto != null){
							service.updateAutomotive(auto);
							out.println("Your update has been received! Please continue with other options");
						}else
							err.println("You should select automotive first!");
						break;
					case "5":
						out.println("Enter Automotive Name:");
						input = br.readLine();
						service.deleteAutomotive(input);
						out.println("Your delete has been received! Please continue with other options");
						break;
					case "6":
						if (auto != null) {
							out.println(service.listOptions(auto));
						} else
							err.println("You should select automotive first!");
						break;
					case "7":
						if (auto != null) {
							out.println("Enter in the format[no space please]: set name,option name");
							input = br.readLine();
							tokens = input.split(",");
							service.configure(auto, tokens[0], tokens[1]);
							out.println("Your update has been received! Please continue with other options");
						} else
							err.println("You should select automotive first!");
						break;
					case "8":
						if (auto != null)
							out.println("The price is: " + service.getPrice(auto));
						else
							err.println("You should select automotive first!");
						break;
					case "9":
						if (auto != null)
							out.println(service.printCar(auto));
						else
							err.println("You should select automotive first!");
						break;
					default:
						err.println("Err command, please enter l to view the menu first");
						break;
					}
				} catch (OptionException | AutomotiveException e) {
					err.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			err.println(e.getMessage());
		}
	}

	public static void listOptions() {
		out.println("Enter q to quit, l to list menus:");
		/* for automotive */
		out.println("1.load automotives");
		out.println("2.list automotives");
		out.println("3.select an automotive");
		out.println("4.update an automotive");
		out.println("5.delete an automotive");
		out.println("6.list options");
		/* for car */
		out.println("7.configure a car");
		out.println("8.get the car's price");
		out.println("9.print the car");
	}

}
