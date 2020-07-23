package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class OAuth2Server extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type = req.getParameter("response_type");
		String redirectUrl = req.getParameter("redirect_url");
		String challenge = req.getParameter("code_challenge");
		Cookie cookie = new Cookie("challenge", challenge);
		cookie.setHttpOnly(true);
		cookie.setSecure(req.isSecure());
		
		resp.addCookie(cookie);
		String code = "ditisdecode";
		
		String location = redirectUrl + "?code=" + URLEncoder.encode(code);
		resp.sendRedirect(location);
//		resp.setContentType("text/html");
//		resp.getWriter().print("<a href='" + location + "'>KLIK</a>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Cookie[] cookies = req.getCookies();
		Cookie challenge = null;
		for (Cookie i: cookies) {
			if (i.getName().equals("challenge")) { challenge = i; }
		}
		challenge.setMaxAge(-1);
		String verifier = req.getParameter("code_verifier");
		String code = req.getParameter("authorization_code");
		if (verifier.equals(challenge.getValue()) && "ditisdecode".equals(code)) {
			resp.addCookie(challenge);
			resp.setContentType("application/json");
			resp.getWriter().print("{'access_token':'okay'}");
		} else {
			resp.sendError(resp.SC_BAD_REQUEST);
		}
	}

	
	
}
