package fi.servlet.dwomaccess;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.xmlrpc.Servlet;
import fi.dwo.server.persistence.DbAccessLocal;

public class DbAccessServlet extends Servlet {

	public DbAccessServlet() {
		super(new fi.dwo.server.persistence.DbAccess());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * Access-Control-Allow-Origin: http://foo.example
		 * Access-Control-Allow-Methods: POST, GET, OPTIONS
		 * Access-Control-Allow-Headers: Origin, content-type
		 */
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
		resp.setHeader("Access-Control-Expose-Headers", "content-type");
		resp.setHeader("Access-Control-Allow-Headers", "origin, content-type");

		resp.setContentType("text/plain");
		resp.getOutputStream().close();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if("POST".equals(req.getMethod()))
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Access-Control-Expose-Headers", "content-type");
		logHeaders(req);
		super.service(req, resp);
	}

	private void logHeaders(HttpServletRequest req) {
		Enumeration e = req.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Enumeration values = req.getHeaders(key);
			while (values.hasMoreElements()) {
				Object object = (Object) values.nextElement();
				log (key + ": " + object);
			}
		}
		
	}

}
