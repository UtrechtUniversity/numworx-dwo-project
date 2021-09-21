package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.Collections;
import java.util.List;

import org.fusesource.restygwt.client.MethodCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestMethod;

public interface MethodRestCaller  {
	void getMethod(String id, RestMethod rest, MethodCallback<DomMethod> callback);
	default void getList(String id, RestContext rest, MethodCallback<List<DomMethod>> callback) {
		callback.onSuccess(null, Collections.emptyList());
	}
}
