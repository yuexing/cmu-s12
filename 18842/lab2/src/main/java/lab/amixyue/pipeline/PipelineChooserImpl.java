package lab.amixyue.pipeline;

import lab.amixyue.constant.Global;
import lab.amixyue.constant.PipelineType;
import lab.amixyue.context.Context;
import lab.amixyue.context.Session;
import lab.amixyue.pipeline.valve.*;
import lombok.extern.log4j.Log4j;

@Log4j
public class PipelineChooserImpl implements PipelineChooser {

	private Context context;

	// how to pass context through pipeline
	public PipelineChooserImpl(Context context) {
		this.context = context;
	}

	/**
	 * Clock: user action cause clock update.
	 * eg. user type sendto update once
	 * user type recv update once
	 */
	public void choose(PipelineType type) {

		Pipeline pipeline = null;
		Session session = context.getSession();

		switch (type) {
		case group:
			session.setAttribute(Global.prefix+"sendMsg",
					context.getAttribute(Global.prefix+"sendMsg"));
			pipeline = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new SendMsgCheckValve(this),
							new SendIdValve(this), new SendClockValve(this),
							new BuiGroupMsgValve(this),
							new UpCtxtByGroupValve(this),
							new SendGroupValve(this),
							new SendRuleValve(this),
							new SendValve(this) };
					return this.valves;
				}
			};
			break;
		case gstart:
			session.setAttribute(Global.prefix+"sendMsg",
					context.getAttribute(Global.prefix+"sendMsg"));
			pipeline = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new SendMsgCheckValve(this),
							new SendIdValve(this),
							new SendClockValve(this),
							new GStartValve(this), 
							new SendGroupValve(this), 
							new SendValve(this) };
					return this.valves;
				}
			};
			break;
		case gend:
			session.setAttribute(Global.prefix+"sendMsg",
					context.getAttribute(Global.prefix+"sendMsg"));
			pipeline = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new SendMsgCheckValve(this),
							new SendIdValve(this),
							new SendClockValve(this),
							new GEndValve(this), 
							new SendGroupValve(this), 
							new SendValve(this) };
					return this.valves;
				}
			};
			break;
		case recvGroup:
			session.setAttribute(Global.prefix+"sendMsg",
					context.getAttribute(Global.prefix+"recvfMsg"));
			pipeline = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new UpCtxtByGroupValve(this) };
					return this.valves;
				}
			};
			break;
		
		case recvCtrl:
			session.setAttribute(Global.prefix+"recvMsg",
					context.getAttribute(Global.prefix+"recvfMsg"));
			pipeline = new Pipeline() {

				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new RecvChkValve(this),
							//new RecvPresendValve(this),
							new RecvBufChkValve(this),
							new RecvReqValve(this),
							new RecvAckValve(this),
							new RecvReleaseValve(this),
							new RecvPresendValve(this),
							new SendChkValve(this),
							new SendGroupValve(this), new SendRuleValve(this),
							new SendValve(this) };
					return valves;
				}
			};
			break;
		case recvAck:
			session.setAttribute(Global.prefix+"recvMsg",
					context.getAttribute(Global.prefix+"recvfMsg"));
			pipeline = new Pipeline() {

				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { };
					return valves;
				}
			};
			break;
			//send request to everyone
			// is it a group multicast?
			// make it a multicast in case of TA
		
		case receive:
			// session.setAttribute("", context.getAttribute(""));
			pipeline = new Pipeline() {

				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new RecvRuleValve(this),
							new RecvClockValve(this), new RecvValve(this) };
					return valves;
				}
			};
			// first check time stamp and log thrown msg
			// take care about marker
			// the lowest level valve get a msg from the queue
			// and find out it is a marker
			// thus, it will continue getting msgs until a non-marker one.
			// so, there needs a new marker queue?
			// the next valve should be a markValve, who look at marker queue
			// and do marker things
			// until the marker queue is empty.
			// if wanna a new pipeline, a new thread! or deadlock
			break;
		case marker:
			break;
		case send:
			session.setAttribute(Global.prefix+"sendMsg",
					context.getAttribute(Global.prefix+"sendMsg"));
			pipeline = new Pipeline() {
				@Override
				public Valve[] buildPipeline() {
					this.valves = new Valve[] { new SendMsgCheckValve(this),
							new SendIdValve(this), new SendClockValve(this),
							new SendChkValve(this),
							new SendReqValve(this),
							new SendGroupValve(this),							
							new SendRuleValve(this), 
							new SendValve(this) };
					return valves;
				}
			};
			log.debug("enter pipeline send");
			break;
		
		}

		pipeline.process(session);
	}
}
