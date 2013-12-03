package lab.amixyue.context;

import lombok.Getter;
import java.util.*;
public class MapContext extends AbstractContext {

	@Getter
	private HashMap<String, ArrayList<HashMap<String, Object>>> map;
	
	public MapContext(HashMap<String, ArrayList<HashMap<String, Object>>> map){
		super.name = name;
		this.map = map;
	}
	@Override
	public synchronized void buildContext() {
		// get context from a map
	}

}
