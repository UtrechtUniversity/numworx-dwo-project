package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;

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
		// TODO Auto-generated method stub
		
	}

}
