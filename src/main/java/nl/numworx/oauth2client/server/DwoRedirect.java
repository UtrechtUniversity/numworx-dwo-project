package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fi.dwo.commons.util.DatatypeConverter;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.LoginManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

@SuppressWarnings("serial")
public class DwoRedirect extends HttpServlet {

	private URL serverUrl;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String state = (String) session.getAttribute("dwologin.state");
		if (state == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		int komma = state.indexOf(';');
		String url = state.substring(0, komma);
		state = state.substring(komma+1);
		
		String nonce = (String) session.getAttribute("dwologin.nonce");
		try {
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			if (nonce != null && nonce.equals(req.getParameter("nonce"))) {
				String code = getBearerToken(username, password);
				url += "?state=" + state;
				//session.setAttribute("dwologin.code", code);
				session.removeAttribute("dwologin.nonce");
				session.removeAttribute("dwologin.state");
				url += "&code="  + code;			
				resp.sendRedirect(url);
				return;
			}
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch(Dwo2Exception d) {
			Dwo2ExceptionCode code = d.getDwo2Code();
			log("doPost login exception " + code, d);
			String form = DwoLogin.getLoginForm();
			resp.setContentType("text/html");
			resp.getWriter().format(form, nonce, d.getLocalizedMessage());
		}
	}

	private String getBearerToken(String username, String password) throws ServletException, Dwo2Exception {
		
		try {
			DomUserFullwLoginContext info = LoginManager.basicLogin(username, md5(password));
			DomContext context = new DomContext();
			
			DomHasRole hasRole = new DomHasRole();
			hasRole.setId(info.getDomLoginContext().getHasRoleId());
			hasRole.setUserId(info.getDomUserFull().getId());
			hasRole.setSchoolGroupId(info.getDomLoginContext().getSchoolGroupId());
			context.setDomHasRole(hasRole);
			context.setRealm(info.getDomLoginContext().getRealm());
			StoredRestManager.getInstance().getAuthenticator().setContext(context);
			return SecureUserAccountManager.getBearerToken();
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		}
	}

	private String md5(String password) throws NoSuchAlgorithmException {
		MessageDigest md5;
		md5 = MessageDigest.getInstance("MD5");		
		byte[] digest = (md5.digest(password.getBytes()));
		return DatatypeConverter.printHexBinary(digest).toLowerCase();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext context = getServletContext();
		String dbrest_url = context.getInitParameter("dbrest.url");
	    RestAuthenticator authenticator = StoredRestManager.getInstance().getAuthenticator();
		try {
			authenticator.setServerUrlPath(serverUrl = new URL(dbrest_url));
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String auth = req.getHeader("Authorization");
		HttpSession session = req.getSession();
		if (auth == null) auth = "None";
		try {
			StoredRestManager manager = new StoredRestManager(new RestAuthenticator());
			manager.getAuthenticator().setServerUrlPath(serverUrl); // copy 
			if (auth.toLowerCase().startsWith("bearer "))
				manager.setBearerAuthString(auth.substring(7));
			else if (auth.toLowerCase().startsWith("basic ")) {
				String[] up = new String(Base64.getDecoder().decode(auth.substring(6))).split(":", 2);
				manager.setBasicAuthString(up[0], up[1], null);
			} else {
				session.removeAttribute("dwologin.code");
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			String code = SecureUserAccountManager.getBearerToken(manager);
			session.setAttribute("dwologin.code", code);
			resp.sendError(HttpServletResponse.SC_NO_CONTENT);
		} catch (Exception e) {
			auth = auth + " / " + StoredRestManager.getInstance().getBasicAuthString();
			log("getBearerToken " + auth, e);
			session.removeAttribute("dwologin.code");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} 
	}

}
