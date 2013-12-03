package lab.amixyue.receive;

import lab.amixyue.context.Context;
import lab.amixyue.pipeline.PipelineChooser;

public abstract class RecvController implements Runnable{

	protected Context context;
	protected PipelineChooser pipelineChooser;
	/**
	 * run:
	 * start service with a ServerSocket or a DatagramSocket
	 * loop accept and receive 
	 * add to recvQueue only
	 */
}
