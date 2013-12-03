package beauty.web.controller;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import beauty.web.model.User.Type;

public class ActionDispatcher {

	private static final Logger log = Logger.getLogger(ActionDispatcher.class);
	//
	// Class methods to manage dispatching to Actions
	//
	private Map<String, Action> hash = new HashMap<String, Action>();

	// permission control
	private List<String> adminActions = new ArrayList<String>();
	private List<String> manuActions = new ArrayList<String>();
	private List<String> retailActions = new ArrayList<String>();

	public void add(Action a) {
		synchronized (hash) {
			if (hash.get(a.getName()) != null) {
				throw new AssertionError("Two actions with the same name ("
						+ a.getName() + "): " + a.getClass().getName()
						+ " and " + hash.get(a.getName()).getClass().getName());
			}
			hash.put(a.getName(), a);

			for (Type t : a.getTypes()) {
				switch (t) {
				case admin:
					adminActions.add(a.getName());
					break;
				case manufacturer:
					manuActions.add(a.getName());
					break;
				case retail:
					retailActions.add(a.getName());
					break;
				default:
					log.warn("Unknown type " + t + " for " + a.getName());
				}
			}
		}
	}

	public String perform(String name, HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		Action a;
		synchronized (hash) {
			a = hash.get(name);
		}

		if (a == null)
			return null;
		return a.perform(request, response);
	}

	public boolean isAllow(Type type, String a, List<String> errors) {
		switch (type) {
		case admin:
			if (adminActions.contains(a)) {
				return true;
			} else {
				errors.add("Permission Denied " + type + ": " + a);
				return false;
			}
		case manufacturer:
			if (manuActions.contains(a)) {
				return true;
			} else {
				errors.add("Permission Denied " + type + ": " + a);
				return false;
			}
		case retail:
			if (retailActions.contains(a)) {
				return true;
			} else {
				errors.add("Permission Denied " + type + ": " + a);
				return false;
			}
		default:
			log.warn("Unknown type " + type + " for " + a);
			errors.add("Unknown type " + type + " for " + a);
			return false;
		}
	}
}
