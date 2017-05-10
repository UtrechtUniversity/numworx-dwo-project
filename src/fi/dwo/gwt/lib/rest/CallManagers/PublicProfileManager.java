package fi.dwo.gwt.lib.rest.CallManagers;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.PublicProfileRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class PublicProfileManager {

	PublicProfileRestCaller service = GWT.create(PublicProfileRestCaller.class);
	
	public Promise<DomDwoProfileFull> get(String name) {
		PromiseCallback<DomDwoProfileFull> result = new PromiseCallback<DomDwoProfileFull>();
		service.get(name, result);
		return result.getPromise();
	}
	
	public Promise<DomDwoProfileFull> get(long id) {
		return get(Long.toString(id));
	}

	public Promise<DomDwoProfileFull> get(int id) {
		return get(Integer.toString(id));
	}
}
