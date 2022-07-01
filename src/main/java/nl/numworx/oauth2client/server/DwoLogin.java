package nl.numworx.oauth2client.server;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

class DwoLogin implements Login {

	public DwoLogin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge, Boolean asr)
			throws Exception {
		HttpSession session = req.getSession();
		session.setAttribute("dwologin.state", state);
		Cookie cookie = new Cookie(CHALLENGE, codeChallenge);
		cookie.setHttpOnly(true);
		cookie.setSecure(req.isSecure());
		cookie.setPath("/");
		resp.addCookie(cookie);

		Reader reader = new InputStreamReader(getClass().getResourceAsStream("/DwoLogin.html"));
		char buffer[] = new char[2048];
		StringBuilder html = new StringBuilder();
		int size;
		while ( (size = reader.read(buffer, 0, buffer.length)) > 0) {
			html.append(buffer, 0, size);
		}
		String format = html.toString();
		String nonce = "nonce";
		session.setAttribute("dwologin.nonce", nonce);
		resp.setContentType("text/html");
		resp.getWriter().format(format, nonce);
	}

}
