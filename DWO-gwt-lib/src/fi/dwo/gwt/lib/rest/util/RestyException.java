package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;

@SuppressWarnings("serial")
public class RestyException extends Exception {
	final public Method method;

	protected RestyException(Method method, Throwable t) {
		super(t.getMessage(),t);
		this.method = method;
	}
	
}
