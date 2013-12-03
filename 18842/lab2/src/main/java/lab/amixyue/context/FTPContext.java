package lab.amixyue.context;

import lombok.Getter;

public class FTPContext extends AbstractContext{

	@Getter
	private String path;
	
	public FTPContext(String name, String path){
		this.path = path;
		super.name = name;
	}
	
	public synchronized void buildContext(){
		// TODO get context from ftp
	}

}
