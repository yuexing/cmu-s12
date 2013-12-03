package lab.amixyue.pipeline.valve;

import java.util.Arrays;
import lab.amixyue.context.*;
import lab.amixyue.pipeline.Pipeline;

public abstract class Valve {

	protected boolean broken = false;
	protected String brokeStr;
	// current pipe
	protected Pipeline curPipe;
	private Pipeline subPipe;

	public abstract void process(Session session);

	public Valve(Pipeline curPipe) {
		this.curPipe = curPipe;
	}

	public boolean isBroken() {
		return broken;
	}

	public String getBrokeStr() {
		return brokeStr;
	}

	/**
	 * get sub pipeline from next valve
	 * 
	 * @return
	 */
	protected Pipeline subPipe() {
		if (subPipe == null)
			subPipe = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					Pipeline curPipe = Valve.this.curPipe;
					Valve curValve[] = curPipe.getValves();
					int index = 0;
					for (Valve v : curValve) {
						if (Valve.this.equals(v))
							break;
						index++;
					}
					Valve[] tmp = Arrays.copyOfRange(curValve, ++index,
							curValve.length);
					return tmp;
				}
			};
		return subPipe;
	}
}
