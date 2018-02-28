package fi.dwo.server.rest.jaxrsfilters;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;


@Provider
@Priority(Priorities.AUTHORIZATION)
@AssertUser
public class AuthenticationAssertFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    final SecurityContext securityContext = requestContext.getSecurityContext();
    Principal user = securityContext.getUserPrincipal();
    if(user == null) {
        abortUnauthorized(requestContext);
    }
  }

  private void abortUnauthorized(ContainerRequestContext context) {
    Response response = 
            Response.status(Response.Status.UNAUTHORIZED)
            .build();
    
    context.abortWith(response);
}

}
