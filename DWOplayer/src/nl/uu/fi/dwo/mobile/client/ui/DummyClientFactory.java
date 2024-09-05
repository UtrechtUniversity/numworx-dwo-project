package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;

import com.google.gwt.place.shared.Place;

@Singleton
public class DummyClientFactory implements ClientFactory {

	
	@Inject DummyClientFactory() {
    }

	@Override
	public Promise<Void> logout() {
		return null;
	}

	@Override
	public void gotoCourses() {
	}

	@Override
	public void goTo(Place next) {
	}

}
