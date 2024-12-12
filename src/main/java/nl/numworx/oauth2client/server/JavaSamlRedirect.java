package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.servlet.lti.RestHandler;

@SuppressWarnings("serial")
public class JavaSamlRedirect extends HttpServlet {
	private RestHandler rest;
	private String org_id;
	private String schoolID;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JavaSamlLogin login = new JavaSamlLogin(getServletConfig());
		try {
			if (login.authenticate(req, resp)) {
				String user_id = login.getStudentNumber();
				if (user_id == null || user_id.isEmpty() )
					user_id = login.getUid();
				String lti_id = login.getUid(); // should be token.sub
				String first = login.getGivenName();
				String middle = login.getInsertion();
				String last = login.getSn();
				String email = login.getEmail();
				String roles = login.getAffiliation();
			    String role = "STUDENT";
			      if(roles != null && roles.toLowerCase().contains("employee"))
			          role = "TEACHER";
				
				String authToken = rest.registerSAML(user_id, lti_id, org_id, first, middle, last, email, role, schoolID, "");
				
				String token = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
				token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
				
				String code = token;
				Cookie cookie = new Cookie(Login.CHALLENGE, login.getNonce());
				cookie.setHttpOnly(true);
				cookie.setSecure(req.isSecure());
				cookie.setPath("/");
				resp.addCookie(cookie);
				String state = login.getState();
				int index = state.indexOf(';');
				String redirectUri = state.substring(0, index);
				state = state.substring(index+1);
				redirectUri += "?state=" + URLEncoder.encode(state);
				redirectUri += "&code="  + URLEncoder.encode(code);
				log(redirectUri);
				resp.sendRedirect(redirectUri);
			} else {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		} catch (Exception e) {
			log("doPost", e);
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	
	}

	@Override
	public void init() throws ServletException {
		schoolID = Login.schoolid;
		org_id = "saml:" + schoolID;
		ServletContext context = getServletContext();
		String dbrest_url = context.getInitParameter("dbrest.url");
		
		if(dbrest_url != null)
			try {
				rest = new RestHandler(dbrest_url);
				
			} catch (MalformedURLException e) {
				log("dbrest.url", e);
			}
	}

}
