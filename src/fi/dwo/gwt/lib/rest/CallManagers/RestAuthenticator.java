package fi.dwo.gwt.lib.rest.CallManagers;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.http.client.RequestBuilder;

class RestAuthenticator implements DispatcherFilter {
	String username;
	String password;
	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		boolean haspassword = username != null && password != null;
		if(haspassword)builder.setPassword(password);
		if(haspassword)builder.setUser(username);
		builder.setIncludeCredentials(haspassword);
		return true;
	}
}