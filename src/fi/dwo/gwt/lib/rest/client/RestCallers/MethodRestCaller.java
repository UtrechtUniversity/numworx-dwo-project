package fi.dwo.gwt.lib.rest.client.RestCallers;

import org.fusesource.restygwt.client.MethodCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestMethod;

public interface MethodRestCaller  {
	void getMethod(String id, RestMethod rest, MethodCallback<DomMethod> callback);
}
