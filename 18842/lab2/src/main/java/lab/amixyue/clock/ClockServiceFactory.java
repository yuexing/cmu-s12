package lab.amixyue.clock;

import lab.amixyue.context.Context;

public interface ClockServiceFactory {

	ClockService getClock(Context context);
}
