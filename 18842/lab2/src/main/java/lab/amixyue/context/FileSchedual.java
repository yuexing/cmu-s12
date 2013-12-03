package lab.amixyue.context;

import java.util.Timer;

/**
 * once the config file change, update the context
 * 
 * @author amy
 * 
 */
public class FileSchedual extends Timer {

	ContextBuilder contextBuilder;
	TimeStep timeStep;
	String path;

	public FileSchedual(String path, ContextBuilder contextBuilder) {
		this.contextBuilder = contextBuilder;
		this.timeStep = new TimeStep();
		this.path = path;
	}

	public void start() {
		schedule(new FileTask(path) {

			@Override
			public void run() {
				// log.debug("checkFileUpdate");
				if (this.checkFileUpdate()) {
					contextBuilder.buildContext();					
				}
				FileSchedual.this.start();
			}
		}, timeStep.next());

	}
}
