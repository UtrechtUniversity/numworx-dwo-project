package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import fi.servlet.lti.RestHandler;
import io.jsonwebtoken.Claims;

public class RedirectServlet extends HttpServlet {

	private static final String CHALLENGE = Login.CHALLENGE;
	final static private String schoolid = Login.schoolid;

	private RestHandler rest;
	private String schoolID, org_id;
	private UULogin login;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String state = req.getParameter("state");
		String scope = req.getParameter("scope");
		String code = req.getParameter("code");
		
		if (! UULogin.numworx_scope.equals(scope)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		int index = state.indexOf(';');
		String redirectUri = state.substring(0, index);
		state = state.substring(index+1);
		try {
			UULogin.UUClaims claims = login.getToken(code);
			
			// iets dat lijkt op dbaccess.
			String user_id = claims.studentNumber;
			if (user_id == null || user_id.isEmpty() )
				user_id = claims.uid;
			String lti_id = claims.uid; // should be token.sub
			String first =  Objects.toString(claims.givenName, "");
			String middle = Objects.toString(claims.insertion, "");
			String last =   Objects.toString(claims.sn, "");
			String email =  Objects.toString(claims.email, "");
			String roles = claims.affiliation;
		    String role = "STUDENT";
		      if(roles != null && roles.toLowerCase().contains("employee"))
		          role = "TEACHER";
			
			String authToken = rest.registerSAML(user_id, lti_id, org_id, first, middle, last, email, role, schoolID, "");
			
			String token = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
			token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
			
			code = token;
			Cookie cookie = new Cookie(CHALLENGE, claims.nonce);
			cookie.setHttpOnly(true);
			cookie.setSecure(req.isSecure());
			cookie.setPath("/");
			resp.addCookie(cookie);
			redirectUri += "?state=" + state;
			redirectUri += "&code="  + code;
			log(redirectUri);
			HttpSession session = req.getSession(false);
			if (session != null) {
				session.removeAttribute(Login.OAUTH2_PROMPT);
				OAuth2Filter.newsession(session);
			}
			resp.sendRedirect(redirectUri);

		} catch (OAuthSystemException | OAuthProblemException e) {
			log("doGet", e);
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		}		
	}

	@Override
	public void init() throws ServletException {
		schoolID = schoolid;
		org_id = "oidc:" + schoolID;
		ServletContext context = getServletContext();
		String dbrest_url = context.getInitParameter("dbrest.url");
		
		if(dbrest_url != null)
			try {
				rest = new RestHandler(dbrest_url);
				
			} catch (MalformedURLException e) {
				log("dbrest.url", e);
			}
		login = new UULogin(getServletConfig());

	}

}
