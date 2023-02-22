package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.simple.parser.ParseException;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.servlet.lti.RestHandler;
import io.jsonwebtoken.Claims;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class EntreeSRedirect extends HttpServlet {
	private static final String CHALLENGE = "dwoSAMLchallenge";

	private RestHandler rest;
    RestAuthenticator authenticator;
    StoredRestManager restManager;
    SystemManager systemManager;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String state = req.getParameter("state");
		String scope = req.getParameter("scope");
		String code = req.getParameter("code");
		String schoolID, org_id;
		EntreeSLogin login = new EntreeSLogin(getServletConfig());
		
		
		int index = state.indexOf(';');
		String redirectUri = state.substring(0, index);
		state = state.substring(index+1);
		try {
			Claims claims = login.getToken(code); // verify aud=clientid, nonce, sub bewaren, iss = entree-s
			claims = login.userInfo();
			
			// iets dat lijkt op dbaccess.
			String user_id = login.studentNumber;
			if (user_id == null || user_id.isEmpty() )
				user_id = login.uid;
			user_id = user_id.replace('@', '%');
			String lti_id = claims.getSubject();
			String first = Objects.toString(login.givenName, "");
			String middle = Objects.toString(login.insertion, "");
			
			String last = Objects.toString(login.sn, "");
			String email = Objects.toString(login.email, "");
			String roles = login.affiliation;
		    String role = "STUDENT";
		      if(roles != null && roles.toLowerCase().contains("employee"))
		          role = "TEACHER";
			schoolID = claims.get("nlEduPersonHomeOrganizationId", String.class);
			if (schoolID == null) schoolID = System.getProperty("ENV_ORGID", login.client_id);
			org_id = "oauth2:" + schoolID;

			String className = claims.get("nlEduPersonUnit", String.class);
			className = Objects.toString(className, "");
			// if schoolid = SURF then schoolid = @suffix van uid
			
			
			// convert schoolID (BRIN) to number, DWO2Exception if not found
			DomSchoolFull fullschool = systemManager.getSchool(schoolID);
			Long l = MySQLPersistenceId.getNativeId(fullschool);
			schoolID = l.toString();
			String authToken = rest.registerSAML(user_id, lti_id, org_id, first, middle, last, email, role, schoolID, className);
			
			String token = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
			token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
			
			code = token;
			Cookie cookie = new Cookie(CHALLENGE, login.nonce);
			cookie.setHttpOnly(true);
			cookie.setSecure(req.isSecure());
			cookie.setPath("/");
			resp.addCookie(cookie);
			redirectUri += "?state=" + state;
			redirectUri += "&code="  + code;
			log(redirectUri);
			resp.sendRedirect(redirectUri);

		} catch (OAuthSystemException | OAuthProblemException | ParseException e) {
			log("doGet", e);
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (Dwo2Exception e) {
			log("do get school", e);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}		
	}

	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		String dbrest_url = context.getInitParameter("dbrest.url");
		
		if(dbrest_url != null)
			try {
			    authenticator.setServerUrlPath(new URL(dbrest_url));
				rest = new RestHandler(dbrest_url);
				
			} catch (MalformedURLException e) {
				log("dbrest.url", e);
			}
	}

	public EntreeSRedirect() {
		  authenticator = new RestAuthenticator();
		  restManager = new StoredRestManager(authenticator);
		  systemManager = new SystemManager(restManager);
	      Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}

}
