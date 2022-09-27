package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Provider;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DelayedActivity<T> extends AbstractActivity implements Success<T,T>{

	private AcceptsOneWidget panel;
	private EventBus eventBus;
	private final Provider<Activity> provider;
	private Activity delegate;

	public DelayedActivity(Provider<Activity> p) {
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
		delegate = provider.get();
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
	
}
