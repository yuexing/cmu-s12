package lab.amixyue.handler;

import lab.amixyue.model.Message;
/**
 * @author amy
 *
 */
public class Handler {

	private Handler handler;
	
	public Handler(Handler handler){
		this.handler = handler;
	}
	
	public void handlin(Message msg){
		if(handler != null) handler.handlin(msg);
	}
	
	public void handlout(Message msg){
		if(handler != null) handler.handlout(msg);
	}
}
