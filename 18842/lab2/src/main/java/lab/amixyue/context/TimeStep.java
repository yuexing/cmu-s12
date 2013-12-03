package lab.amixyue.context;

import java.util.Calendar;
import java.util.Date;

public class TimeStep {
	
	private Calendar calendar = Calendar.getInstance();
	private int unit = Calendar.SECOND;
	private int period = 2;
	
	public TimeStep(){
		
	}
	
	public TimeStep(int period){
		this.period = period;
	}
	
	public TimeStep(int unit, int period){
		this.unit = unit;
		this.period = period;
	}
	
	
	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public Date next(){
		calendar.add(unit, period);
		return calendar.getTime();
	}
}
