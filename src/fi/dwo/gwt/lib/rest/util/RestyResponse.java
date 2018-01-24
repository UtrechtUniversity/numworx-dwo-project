package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;

public class RestyResponse<T> {
	final public Method method;
	final public T value;

	public RestyResponse(Method method, T value) {
		super();
		this.method = method;
		this.value = value;
	}
}
