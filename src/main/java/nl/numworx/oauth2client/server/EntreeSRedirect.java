package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

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
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class EntreeSRedirect extends HttpServlet {
	private static final String CHALLENGE = "dwoSAMLchallenge";
	static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";

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
		EntreeSLogin login = createLogin();
		
		
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
			if (user_id != null)
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
			if (schoolID == null) {
				schoolID = claims.get("schac_home_organization", String.class);
			}
			if (schoolID == null) schoolID = System.getProperty("ENV_ORGID", login.client_id);
			org_id = "oauth2:" + schoolID;

			String className = claims.get("nlEduPersonUnit", String.class);
			className = Objects.toString(className, "");
			// if schoolid = SURFIN then schoolid = @suffix van uid
			if ("SURFIN".equals(schoolID)) {
				int lastindex = user_id.lastIndexOf('%');
				schoolID = user_id.substring(lastindex+1);
			}

			// stap1 kijk of er al een link is:
			   DomSamlUser u = new DomSamlUser();
			   u.setSamlOrgId(s(org_id));
			   u.setSamlUserId(s(lti_id));
			   try {
				   u = systemManager.requestSamlToken(u);
				   String authToken = u.getAuthToken();
				   redirect(req, resp, state, org_id, login, redirectUri, lti_id, authToken);
				   return;
			   } catch (Dwo2Exception e) {
				   
			   }
			
			cookie("givenName", first, resp);
			cookie("insertion", middle, resp);
			cookie("familyName",last, resp);
			cookie("email", email, resp);
			// convert schoolID (BRIN) to number, DWO2Exception if not found
			DomSchoolFull fullschool = systemManager.getSchool(schoolID);
			cookie("schoolGroup", role, resp);
			
			cookie(DWO_SAML_ORGANIZATION_ID, org_id, resp);
			cookie(DWO_SAML_USER_ID, lti_id, resp);
			if (fullschool != null) {
				cookie("schoolLogin", fullschool.getSchoolLogin(), resp);
				cookie("schoolCode", getSchoolCode(fullschool, role), resp);
			} else {
				cookie("schoolLogin", null, resp);
				cookie("schoolCode", null, resp);
				
			}
			cookie("cancel", redirectUri, resp);
			cookie("next", redirectUri + "?with=" + login, resp);
			cookie("className", className, resp);
			String sugg = first + middle + last;
			sugg = validUsername(sugg);
			// alleen A-Za-z0-9 en - . _
			if (fullschool == null || login.uid == null) // criterium.... bijv. alles ingevuld...
				cookie("suggestion", systemManager.getSuggestion(sugg), resp);
			else
				cookie("username" , user_id + "@" + fullschool.getSchoolLogin(), resp);
			if (true) {
				resp.sendRedirect("/dwo/register/Register.html");
				return;
			}
			

			if (fullschool == null) {
				resp.setContentType("text/plain");
				PrintWriter out = resp.getWriter();
				out.println("uid " + user_id);
				out.println("first " + first);
				out.println("middle " + middle);
				out.println("last " + last);
				out.println("email " + email);
				out.println("role " + roles);
				out.println("class " + className);
				out.println("schoolid " + schoolID);
				out.println("orgid " + org_id);
				out.println("claims " + claims);
				return;
			
			}
			Long l = MySQLPersistenceId.getNativeId(fullschool);
			schoolID = l.toString();
			String authToken = rest.registerSAML(user_id, lti_id, org_id, first, middle, last, email, role, schoolID, className);
			
			redirect(req, resp, state, org_id, login, redirectUri, lti_id, authToken);

		} catch (OAuthSystemException | OAuthProblemException | ParseException e) {
			log("doGet", e);
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (Dwo2Exception e) {
			log("do get school", e);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}		
	}



	protected EntreeSLogin createLogin() {
		return new EntreeSLogin(getServletConfig());
	}



	protected static String validUsername(String sugg) {
		// no spaces or other weird stuff
		if (! SimpleValidUserFieldsChecker.isValidUserName(sugg)) {
			StringBuilder sb = new StringBuilder();
			for (char ch : sugg.toCharArray()) {
				if (validUsername(ch)) sb.append(ch);
			}
			sugg = sb.toString();
			
		}
		return sugg;
	}
	
	static boolean validUsername(char ch) {
		return SimpleValidUserFieldsChecker.isValidUserName("--"+ch);
	}



	private void cookie(String name, String value, HttpServletResponse response) {
		Cookie cookie;  
		if (value != null && !value.isEmpty()) {
			cookie = new Cookie(name, u(value).toString());
		  } else {
			cookie = new Cookie(name, "");
		    cookie.setMaxAge(0);
		  }
		  cookie.setPath("/");
		  response.addCookie(cookie);
		}

	protected void redirect(HttpServletRequest req, HttpServletResponse resp, String state, String org_id,
			EntreeSLogin login, String redirectUri, String lti_id, String authToken) throws IOException {
		String code;
		String token = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
		token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
		
		code = token;
		Cookie cookie = new Cookie(CHALLENGE, login.nonce);
		cookie.setHttpOnly(true);
		cookie.setSecure(req.isSecure());
		cookie.setPath("/");
		resp.addCookie(cookie);
		redirectUri += "?state=" + u(state);
		redirectUri += "&code="  + u(code);
		log(redirectUri);
		resp.sendRedirect(redirectUri);
	}

	private String s(Object o) {
		return Objects.toString(o, "");
	}
	private Object u(Object value) {
		  try {
			  	value = URLEncoder.encode(value.toString(), "UTF-8").replaceAll("\\+", "%20");
			  } catch(Exception e) {}
		  return value;
	}
	public String getSchoolCode(DomSchoolFull school, String role) {
		if (school != null && AboType.premium == school.getAboType()) {
			
			List<DomMapEntry<RoleType, String>> passwords = school.getPasswords();
			if (passwords != null) 
				for (DomMapEntry<RoleType, String> item : passwords) {
				if (role.equals(item.getKey().name()))
					return item.getValue();
			}
		}
		return null;
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
