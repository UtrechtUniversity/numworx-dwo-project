package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.http.client.RequestBuilder;

public class RestAuthenticator implements DispatcherFilter {
	private String username;
	private String password;
	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		boolean haspassword = username != null && password != null;
		if(haspassword)builder.setPassword(password);
		if(haspassword)builder.setUser(username);
		builder.setIncludeCredentials(haspassword);
		return true;
	}
        public void setCredentials(String aUsername, String aPassword){
            username = aUsername;
            password = aPassword;
        }
}