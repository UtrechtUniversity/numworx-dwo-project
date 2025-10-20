package fi.dwo.server.template;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MessageSource;

public class LogoutHtml extends HttpServlet {

	private static final String LOGOUT = "fi.dwo.server.template.logout";
	Map<String, ResourceBundle> rb;
	ResourceBundle rb0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		String locale = (String) req.getAttribute("template.locale");
		ResourceBundle bundle = rb.computeIfAbsent(locale, this::getbundle);
		String logout = bundle.getString("logout");
		String html   = bundle.getString("html");
		String quit   = bundle.getString("quit");
		String title  = bundle.getString("title");
		writer.print(MessageFormat.format(html, logout, quit, title));
	}

	private ResourceBundle getbundle(String key) {
		try {
			return ResourceBundle.getBundle(LOGOUT, Locale.forLanguageTag(key));
		} catch(Exception err) {
			log("getBundle", err);
		}
		return rb0;
	}
	@Override
	public void init() throws ServletException {
		rb0 = ResourceBundle.getBundle(LOGOUT);
		rb = new HashMap<>();
	}


}
