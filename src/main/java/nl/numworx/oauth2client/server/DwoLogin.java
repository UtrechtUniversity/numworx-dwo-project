package nl.numworx.oauth2client.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

class DwoLogin implements Login {

	public DwoLogin(ServletConfig servletConfig) {
	}

	@Override
	public void login(HttpServletRequest req, HttpServletResponse resp, String state, String codeChallenge, Boolean asr)
			throws Exception {
		HttpSession session = req.getSession();
		String code = (String) session.getAttribute("dwologin.code");
		if (code != null) {
			session.removeAttribute("dwologin.code");
			int komma = state.indexOf(';');
			String url = state.substring(0, komma);
			state = state.substring(komma+1);
			url += "?state=" + state;
			url += "&code="  + code;			
			resp.sendRedirect(url);
			return;
		}
		session.setAttribute("dwologin.state", state);
		Cookie cookie = new Cookie(CHALLENGE, codeChallenge);
		cookie.setHttpOnly(true);
		cookie.setSecure(req.isSecure());
		cookie.setPath("/");
		resp.addCookie(cookie);

		String format = getLoginForm();
		byte[] random = new byte[16];
		ThreadLocalRandom.current().nextBytes(random);
		String nonce = Base64.getEncoder().withoutPadding().encodeToString(random);
		session.setAttribute("dwologin.nonce", nonce);
		resp.setContentType("text/html");
		resp.getWriter().format(format, nonce, "");
	}

	static String getLoginForm() throws IOException {
		Reader reader = new InputStreamReader(DwoLogin.class.getResourceAsStream("/DwoLogin.html"));
		char buffer[] = new char[2048];
		StringBuilder html = new StringBuilder();
		int size;
		while ( (size = reader.read(buffer, 0, buffer.length)) > 0) {
			html.append(buffer, 0, size);
		}
		String format = html.toString();
		return format;
	}

}
