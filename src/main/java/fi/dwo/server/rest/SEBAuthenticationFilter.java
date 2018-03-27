package fi.dwo.server.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@RequireSecureExamBrowser
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SEBAuthenticationFilter implements ContainerRequestFilter {

	private static final Logger LOG = Logger.getLogger(SEBAuthenticationFilter.class.getName());
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// inspect headers, SecurityContext, etc...
		String sebHeader = requestContext.getHeaderString("X-SafeExamBrowser-RequestHash");
		if( !sebHeaderValid(sebHeader, requestContext.getUriInfo().getRequestUri())) {
			abortForbidden(requestContext);
			return;
		}
	}

	private void abortForbidden(ContainerRequestContext context) {
		Response response = 
				Response.status(Response.Status.FORBIDDEN)
				.build();		
		context.abortWith(response);
	}

	private boolean sebHeaderValid(String sebHeader, URI requestUri) {
		return sebHeader != null; //TODO implement sha-256 verificatie.
	}


}
