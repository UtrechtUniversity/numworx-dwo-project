package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class NeedLogin<T> implements Function<Promise<?>, Promise<? extends T>> {


	public static <S> NeedLogin<S> instance() {
		if (Actions.isAvailable())
			return new ActionNeedLogin<S>();
		return new NeedLogin<S>();
	}
		
	protected NeedLogin() {}

	@SuppressWarnings("unchecked")
	@Override
	public Promise<T> apply(Promise<?> t) {
		return (Promise<T>) t;
	}

	static class ActionNeedLogin<T> extends NeedLogin<T> {

		@Override
		public Promise<T> apply(Promise<?> t) {
			if (needed(t)) {
				Actions.LOGINNEEDED.execute();
			}
			return super.apply(t);
		}

		public boolean needed(Promise<?> resolved) {
			Throwable t = resolved.getFailure();
			return  (t instanceof Dwo2Exception && Dwo2ExceptionCode.Rest_LoginNeeded == ((Dwo2Exception) t).getDwo2Code());
		}
		
	}

	public boolean needed(Promise<?> resolved) {
		return false;
	}
	
	
	static public class PlaceNeedLogin<T> extends ActionNeedLogin<T> {
		private final GotoController controller;
		private final Place place;

		@Override
		public Promise<T> apply(Promise<?> t) {
			if (needed(t)) {
				controller.goTo(place);
			}
			return (Promise<T>) (t);
		}

		public PlaceNeedLogin(Place place, GotoController controller) {
			this.place = place;
			this.controller = controller;
		}
		
	}
 
}
