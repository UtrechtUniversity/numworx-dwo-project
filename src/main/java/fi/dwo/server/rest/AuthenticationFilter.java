package fi.dwo.server.rest;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final String REALM = "dwo.nl";
	

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		final SecurityContext currentContext = requestContext.getSecurityContext();
// inspect headers, SecurityContext, etc...
		SecurityContext context = currentContext; // Wrap context...
// set for all @Secured classes/methods
		requestContext.setSecurityContext(context);
	}

	private void abortUnauthorized(ContainerRequestContext context) {
		Response response = 
				Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, 
						AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
				.build();
		
		context.abortWith(response);
	}
	
}
