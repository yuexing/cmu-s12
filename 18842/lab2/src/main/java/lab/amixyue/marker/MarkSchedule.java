package lab.amixyue.marker;

import java.util.Timer;
import java.util.TimerTask;

import lab.amixyue.constant.PipelineType;
import lab.amixyue.context.Context;
import lab.amixyue.context.TimeStep;
import lab.amixyue.pipeline.PipelineChooser;

public class MarkSchedule extends Timer {

	TimeStep timeStep;
	Context context;
	
	public MarkSchedule(Context context){
		//3 min
		timeStep = new TimeStep(180);
		this.context = context;
	}
	
	public void schedule(){
		this.schedule(new TimerTask() {
			
			@Override
			public void run() {
				PipelineChooser chooser = (PipelineChooser) context.getAttribute("pipelineChooser");
				chooser.choose(PipelineType.marker);
				schedule();
			}
		}, timeStep.next());
	}
}
