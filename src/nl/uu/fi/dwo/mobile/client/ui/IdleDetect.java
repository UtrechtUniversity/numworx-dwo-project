package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.core.shared.GWT;
//import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

@Singleton 
public class IdleDetect extends Timer implements NativePreviewHandler {
	
	public final static int FAST = 2;      // 10-20 secs
	public final static int SLOW = 6 * 15; // 15 minutes
	
	final public boolean hasIdle = true;
	
	public interface IdleHandler {
		void onIdle(IdleEvent ev);
	}
	
    public static final Event.Type<IdleHandler> TYPE = new Event.Type<>();
	
	public static class IdleEvent extends Event<IdleHandler> {

	  public  final int cnt;

		@Override
		public Type<IdleHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(IdleHandler handler) {
			handler.onIdle(this);
		}
		
		private IdleEvent(int c ) {
			cnt = c;
		}

		/**
		 * @return the slow
		 */
		public boolean isSlow() {
			return cnt > FAST;
		}

		/**
		 * @return the cnt
		 */
		public int getCnt() {
			return cnt;
		}
		
		public String toString() {
			return "IdleEvent[" + cnt + "]";
		}
		
	}
	private int cnt;
	
	
	private final EventBus bus;
	private HandlerRegistration reg;
	
	@Inject IdleDetect(EventBus bus) {
		this.bus = bus;
	}
	
	public void reset() {
		cnt = 0;
	}
	
	public void start() {
	  if (hasIdle) {
		if (reg == null)
			reg = com.google.gwt.user.client.Event.addNativePreviewHandler(this);
		this.scheduleRepeating(10000); // 10 sec
	  } else {
	    GWT.log("no idleDetect");
	  }
	}
	
	public void stop() {
	  if (hasIdle) {
		cancel();
		if (reg != null) {
			reg.removeHandler(); reg = null;
		}
	  }
	}
	
	public void fire() {
		IdleEvent event = new IdleEvent(cnt);
		//GWT.log("fire " + event);
		bus.fireEvent(event);
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		reset();
	}

	@Override
	public void run() {
		cnt ++;
		if (cnt <= FAST) fire();
		else if (cnt >= SLOW) {
			fire();
            reset();
		} 
		//else {GWT.log("not idle " + cnt);}
	}
	
}
