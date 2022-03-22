package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogEvent;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPromise;
import nl.uu.fi.dwo.mobile.client.ui.places.LogoutPlace;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class NeedLogin<T> implements Function<Promise<?>, Promise<? extends T>>, NeedLoginHandler {

	
	public static <T> NeedLogin<T> instance() { return new NeedLogin<>(); }
	
	protected NeedLogin() {}

	@SuppressWarnings("unchecked")
	@Override
	public Promise<T> apply(Promise<?> t) {
		return (Promise<T>) t;
	}

	static final NeedLoginEvent event = new NeedLoginEvent();
	static class PlaceNeedLogin<T> extends NeedLogin<T> implements NeedLoginHandler {

		Place place;
		final PlaceController controller;
		final EventBus bus;
		final TrafficAgent traffic;
		
		PlaceNeedLogin(Place place, PlaceController controller, EventBus bus2, TrafficAgent traffic) {
			this.place = place;
			this.controller = controller;
			this.bus = bus2;
			this.traffic = traffic;
		}
		
		@Override
		public Promise<T> apply(Promise<?> t) {
			if (needed(t)) {
				traffic.reset(t.getFailure());
				event.resolved = t;
				bus.fireEvent(event);
			}
			return super.apply(t);
		}

		public boolean needed(Promise<?> resolved) {
			Throwable t = resolved.getFailure();
			return  (t instanceof Dwo2Exception && Dwo2ExceptionCode.Rest_LoginNeeded == ((Dwo2Exception) t).getDwo2Code());
		}

		@Override
		public void onNeedLogin(NeedLoginEvent ev) {
			MsgClickedDialogPromise defer = new MsgClickedDialogPromise(event.resolved.getFailure().getLocalizedMessage());
			bus.fireEvent(new MsgClickedDialogEvent(MsgClickedDialogEvent.EventType.MsgClickedDialog, defer));
			defer.getPromise().onResolve(() -> controller.goTo(place));
		}
		
		public void setPlace(Place p) {
			this.place = p;
		}
	}

	public boolean needed(Promise<?> resolved) {
		return false;
	}
	
	
	static public class ActionNeedLogin<T> extends PlaceNeedLogin<T> {


		public ActionNeedLogin(PlaceController controller, EventBus bus, TrafficAgent a) {
			super(LogoutPlace.INSTANCE, controller, bus,a);
		}

		@Override
		public void onNeedLogin(NeedLoginEvent ev) {
			controller.goTo(place);
			Actions.LOGINNEEDED.execute();
		}
		
		public void setPlace(Place place) { }
	}

	@Override
	public void onNeedLogin(NeedLoginEvent ev) {
	}
	public void setPlace(Place place) {
	}
}
