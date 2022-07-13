package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

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
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

@SuppressWarnings("serial")
public class DwoRedirect extends HttpServlet {

	String BR = "<br>";
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
			if (Objects.equals(nonce, req.getParameter("nonce"))) {
				String code = getBearerToken(username, password);
				url += "?state=" + state;
				//session.setAttribute("dwologin.code", code);
				url += "&code="  + code;			
				resp.sendRedirect(url);
				return;
			}
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch(Dwo2Exception d) {
			Dwo2ExceptionCode code = d.getDwo2Code();
			// restart login
			String form = DwoLogin.getLoginForm();
			resp.setContentType("text/html");
			resp.getWriter().format(form, nonce);
		}
	}

	private String getBearerToken(String username, String password) throws ServletException, Dwo2Exception {
		
		try {
			DomUserFullwLoginContext info = LoginManager.basicLogin(username, md5(password));
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
			authenticator.setServerUrlPath(new URL(dbrest_url));
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String auth = req.getHeader("Authorization");
		if (auth.toLowerCase().startsWith("bearer "))
			StoredRestManager.getInstance().setBearerAuthString(auth.substring(7));
		else if (auth.toLowerCase().startsWith("basic ")) {
			String[] up = new String(Base64.getDecoder().decode(auth.substring(6))).split(":", 2);
			StoredRestManager.getInstance().setBasicAuthString(up[0], up[1], null);
		} else {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		try {
			String code =  SecureUserAccountManager.getBearerToken();
			HttpSession session = req.getSession();
			session.setAttribute("dwologin.code", code);
			resp.sendError(HttpServletResponse.SC_NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} 
	}

}
