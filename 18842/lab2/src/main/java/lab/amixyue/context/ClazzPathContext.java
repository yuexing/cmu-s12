package lab.amixyue.context;


public class ClazzPathContext extends AbstractContext{

	public ClazzPathContext(String name){
		super.name = name;
	}
	
	@Override
	public synchronized void buildContext() {
		// TODO get config from classpath
	}

}
