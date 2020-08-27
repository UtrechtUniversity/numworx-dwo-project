package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import fi.servlet.lti.DbAccess;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@SuppressWarnings("serial")
public class OAuth2Server extends HttpServlet {

	class CookieWrap extends HttpServletResponseWrapper {

		  List<Cookie> cookies;
		  
		  public CookieWrap(HttpServletResponse response) {
		    super(response);
		    cookies = new Vector<>(); // Synchronized
		  }

		  @Override
		  public void addCookie(Cookie cookie) {
		    super.addCookie(cookie);
		    cookies.add(cookie);
		  }
		  
		  public Cookie[] getCookies() {
		    return cookies.toArray(new Cookie[cookies.size()]);
		  }
		}

	private static final String CHALLENGE = "dwoSAMLchallenge";
	final static private String schoolid = System.getProperty("ENV_ORGID", "385");

	private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	private static final String DWO_SAML_ORGANIZATION = "dwoSAMLOrganization";    
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";
	private Logger LOG = Logger.getLogger(getClass().getName());
	private DbAccess dbaccess;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type = req.getParameter("response_type");
		if (!"code".equals(type)) {
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
          LOG.log(Level.WARNING, "invalid response_type " + type);
          return;		  
		}
		String redirectUrl = req.getParameter("redirect_uri");
		URI client, server;
		try {
          String requestURL = System.getProperty("ALLOW_ORIGIN", "*");
          if ("*".equals(requestURL))
            requestURL = req.getRequestURL().toString();
          server = new URI(requestURL);
          client = new URI(redirectUrl);
          boolean scheme =  server.getScheme().equals(client.getScheme());
          boolean host   =  server.getHost().equals(client.getHost());
          boolean port   =  server.getPort() == client.getPort();
          if (!scheme || !host || !port) 
            throw new URISyntaxException(redirectUrl, "not a lookalike");        
        } catch (URISyntaxException e) {
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
          LOG.log(Level.WARNING, "invalid redirect_uri " + redirectUrl,e);
          return;
        }				
		String challenge = req.getParameter("code_challenge");
		String state = req.getParameter("state");
		String clientId = req.getParameter("client_id");

		
		Cookie cookie = new Cookie(CHALLENGE, challenge);
		cookie.setHttpOnly(true);
		cookie.setSecure(req.isSecure());
		cookie.setPath("/");
// DEBUGING
//		req.setAttribute("uid", "staff1");

		resp.addCookie(cookie);

		DomSamlUser user = new DomSamlUser();
		CookieWrap wrap = new CookieWrap(resp);
	  	String organization = dbaccess.getOrganization(schoolid);
	  	if ( dbaccess.setUUSAMLCookie(req, wrap, schoolid, organization))
	    	return;
		Cookie[] cookies = wrap.getCookies();
		for(Cookie c : cookies) {
			if (DWO_SAML_ORGANIZATION_ID.equals(c.getName())) user.setSamlOrgId( c.getValue() );
			else if (DWO_SAML_USER_ID.equals(c.getName())) user.setSamlUserId( c.getValue() );
			else if (DWO_SAML_AUTH_TOKEN.equals(c.getName())) user.setAuthToken( c.getValue() );
		}		
		
		String samlUserID = user.getSamlUserId();	      
		String samlOrgID = user.getSamlOrgId();
		String authToken = user.getAuthToken();
		log("getToken " + samlUserID + " " + samlOrgID + " " + authToken + " " + clientId);
		String token = "3\f" + samlUserID + '\f' + samlOrgID + '\f' + authToken;
		token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
		
		String code = token;
		
		String location = client.getRawPath() + "?code=" + URLEncoder.encode(code);
		if (state != null) {
			location += "&state=" + URLEncoder.encode(state);
		}
		location = server.resolve(location).toASCIIString();
		resp.sendRedirect(location);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Cookie[] cookies = req.getCookies();
		Cookie challenge = null;
		for (Cookie i: cookies) {
			if (i.getName().equals(CHALLENGE)) { challenge = i; }
		}
		challenge.setMaxAge(0);
		String verifier = req.getParameter("code_verifier");
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException("should not happen", e);
		}
		byte[] encodedhash = digest.digest(
		  verifier.getBytes(StandardCharsets.UTF_8));
		verifier = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(encodedhash);
				
		String code = req.getParameter("authorization_code");
		String uri =req.getParameter("redirect_uri");
		String id = req.getParameter("client_id");
		if (verifier.equals(challenge.getValue()) 
//				&& "ditisdecode".equals(code)
		) {
			resp.addCookie(challenge);
			resp.setContentType("application/json");
			resp.getWriter().print("{\"access_token\":\"okay\"}");
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	public void init() throws ServletException {
		this.dbaccess = new DbAccess(getServletContext());
	}

	
	
}
