package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Provider;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class DelayedActivity<T> extends AbstractActivity implements Success<T,T>, Failure {

	private AcceptsOneWidget panel;
	private EventBus eventBus;
	private final Function<T, Activity> provider;
	private Activity delegate;

	public DelayedActivity(Function<T, Activity> p) {
		provider = p;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		this.eventBus = eventBus;
		if (delegate != null) delegate.start(panel, eventBus);
	}

	@Override
	public Promise<T> call(Promise<T> t) {
		delegate = provider.apply(t.getValue());
		if (panel != null) delegate.start(panel, eventBus);
		return t;
	}

	@Override
	public String mayStop() {
		if (delegate != null) return delegate.mayStop();
		return super.mayStop();
	}

	@Override
	public void onCancel() {
		if (delegate != null) delegate.onCancel();
		panel = null;
		super.onCancel();
	}

	@Override
	public void onStop() {
		if (delegate != null) delegate.onStop();
		panel = null;
		super.onStop();
	}

	@Override
	public void fail(Promise<?> resolved) throws Exception {
		if (panel != null)
			panel.setWidget(new Label(resolved.getFailure().toString()));
		
	}
	
}
