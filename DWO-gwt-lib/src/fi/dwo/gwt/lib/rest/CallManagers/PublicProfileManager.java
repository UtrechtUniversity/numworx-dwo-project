package fi.dwo.gwt.lib.rest.CallManagers;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

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
	
	public Promise<JSONValue> getDescription(RestDwoProfile rest) {
		PromiseCallback<JSONValue> result = new PromiseCallback<>();
		service.getDescription(rest, result);
		return result.getPromise();
	}

}
