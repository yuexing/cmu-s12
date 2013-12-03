package lab.amixyue.context;

import lombok.Getter;

public class URLContext extends AbstractContext{

	@Getter
	private String path;
	
	public URLContext(String name, String path){
		this.path = path;
		super.name = name;
	}
	
	public synchronized void buildContext() {
		// TODO get context from url
	}

}
