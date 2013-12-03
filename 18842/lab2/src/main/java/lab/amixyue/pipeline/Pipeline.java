package lab.amixyue.pipeline;

import lab.amixyue.context.*;
import lab.amixyue.pipeline.valve.Valve;
import lombok.extern.log4j.Log4j;

/**
 * pipeline's process can be concurrent later.
 * should take care about the map attribute.
 * maybe a snapshot of context for each pipeline.
 * while processing, no init allow. this is a transation.
 * @author amy
 *
 */
@Log4j
public abstract class Pipeline {

	//private Context context;
	protected Valve[] valves;
	
	public Valve[] getValves(){
		return valves;
	}
	
	public void process(Session session){
		try{
			//allow nested
			//pipelines can not run together, there will be a race
			Context.writeinit.lock();
			
			for(Valve v : buildPipeline()){
				v.process(session);
				if(v.isBroken()){
					log.debug(v.getBrokeStr());
					return;
				}
			}
		}finally{
			Context.writeinit.unlock();
		}
		
	}
	
	public abstract Valve[] buildPipeline();
}
