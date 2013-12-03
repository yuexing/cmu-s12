package lab1.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import lab1.exception.AutomotiveException;
import lab1.model.Automotive;
import lab1.service.CRUDService;

/**
 * This model encapsules some common operations of the service. This model
 * should be extended by AdminCRUDService or UserCRUDService.
 * @author amixyue
 *
 */
public abstract class AbstractCRUDService implements CRUDService {

	/* child has access to this */
	protected HashMap<String, Automotive> autos = new LinkedHashMap<String, Automotive>();;
	
	@Override
	public Automotive findAutomotive(String name) throws AutomotiveException {
		Automotive auto = null;
		if(( auto = autos.get(name)) == null){
			throw new AutomotiveException(AutomotiveException.ErrMsg.NO_SUCH);
		}else {
			return auto;
		}
	}
	
	@Override
	public void listAutomotives() {
		for(Automotive auto : this.autos.values()){
			System.out.println(auto);
		}		
	}
	
	@Override
	public String listOptions(Automotive auto) {
		Iterator<String> strs = auto.getOptionSetNamesIterator();
		StringBuilder sb = new StringBuilder();
		sb.append("options: ");
		while(strs.hasNext()){
			sb.append(strs.next()+ ";");
		}
		return sb.toString();
	}
}
