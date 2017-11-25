package fi.dwo.server.rest;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.LoginContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@SecureExamBrowser
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SEBAuthenticationFilter implements ContainerRequestFilter {

	private static final String AUTHENTICATION_SCHEME = "Bearer";
	private static final String REALM = "dwo.nl";
	private static final Logger LOG = Logger.getLogger(SEBAuthenticationFilter.class.getName());
	// inject?
	Genson genson = new GensonProvider().getContext(getClass());
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		final SecurityContext currentContext = requestContext.getSecurityContext();
// inspect headers, SecurityContext, etc...
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		String sebHeader = requestContext.getHeaderString("X-SafeExamBrowser-RequestHash");
		if( !sebHeaderValid(sebHeader, requestContext.getUriInfo().getRequestUri())) {
			abortForbidden(requestContext);
			return;
		}
		if( !isTokenBasedAuthentication(authorizationHeader)) {
			abortUnauthorized(requestContext);
			return;
		}
		String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
		try {
			SecurityContext context = validateToken(token, currentContext);
			requestContext.setSecurityContext(context);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "validateToken", e);
			abortUnauthorized(requestContext);
		}		
	}

	private void abortForbidden(ContainerRequestContext context) {
		Response response = 
				Response.status(Response.Status.FORBIDDEN)
				.build();		
		context.abortWith(response);
	}

	private boolean sebHeaderValid(String sebHeader, URI requestUri) {
		return sebHeader != null;
	}

	static class SEBSecurityContext implements SecurityContext {
		SecurityContext delegate;
		SEBPrincipal principal;

		SEBSecurityContext(SEBPrincipal principal, SecurityContext delegate) {
			this.principal = principal;
			this.delegate = delegate;
		}

		public Principal getUserPrincipal() {
			return principal;
		}

		public boolean isUserInRole(String role) {
			return principal.isUserInRole(role);
		}

		public boolean isSecure() {
			return delegate.isSecure();
		}

		public String getAuthenticationScheme() {
			return AUTHENTICATION_SCHEME;
		}
	}
	
	public static class SEBPrincipal implements Principal {
		private PersistentUser u;
		private RoleType role = RoleType.STUDENT;
		private PersistentClassCourse cc;
		
		SEBPrincipal(PersistentUser u, RoleType role, PersistentClassCourse cc) {
			this.u = u;
			this.role = role;
			this.cc = cc;
		}

		@Override
		public String getName() {
			return u.getUsername();
		}
		
		public boolean isUserInRole(String role) {
			return this.role.name().equals(role);
		}

		public PersistentUser getUser() {
			return u;
		}
	}
	
	/**
	 * validate token: classcourseid,hasroleid,verifier .
	 * use classcourse.getAccessKey(),current time for OTP key, logincontext
	 * validate with HMAC(OTPKEY,token)?
	 * 
	 * @param token a bearer token
	 * @param currentContext
	 * @return SecurityContext with SEBPrincipal
	 * @throws Dwo2Exception
	 */
	
	
	private SecurityContext validateToken(String token, SecurityContext currentContext) throws Dwo2Exception {		
		PersistentUser u;
		RoleType role;
		String[] split = token.split(".",3);
		byte[] header = Base64.getDecoder().decode(split[0]);
		byte[] body  =  Base64.getDecoder().decode(split[1]);
		String auth = split[2];
// FIXME use Id's
		DomHasRole domHasRole = genson.deserialize(body, DomHasRole.class);
		DomClassCourse classCourse = genson.deserialize(header, DomClassCourse.class);

		PersistentHasRolePK hrid = MySQLPersistenceId.getNativeId(domHasRole);
		Long ccid = MySQLPersistenceId.getNativeId(classCourse);
		PersistentClassCourse cc = ClassCourseManager.findEntity(ccid);
		String secret = cc.getAccessKey();
		Long uid = hrid.getUserID();
		u = UserManager.findEntity(uid);
		PersistentLoginContext lc = LoginContextUtilManager.getCurrentLoginContext(u);
		
		// verify(secret, token, lc) 
		role = RoleType.STUDENT; // TODO uit hasrole halen
		
		SEBPrincipal principal = new SEBPrincipal(u, role, cc);
		return new SEBSecurityContext(principal, currentContext);
	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		return authorizationHeader != null  && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase());
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
