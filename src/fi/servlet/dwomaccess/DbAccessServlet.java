package fi.servlet.dwomaccess;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.xmlrpc.Servlet;

public class DbAccessServlet extends Servlet {

	public DbAccessServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		//unlock();??????
		setHandler(
				new DbAccessWrapper
				(DbAccessFactory.getDbAccess(getServletContext())));
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
		try {
			//logHeaders(req);
			super.service(req, resp);
		} catch (RuntimeException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "service " + req.getRequestURI(), e);
		}
	}

//	private void logHeaders(HttpServletRequest req) {
//		Enumeration e = req.getHeaderNames();
//		while (e.hasMoreElements()) {
//			String key = (String) e.nextElement();
//			Enumeration values = req.getHeaders(key);
//			while (values.hasMoreElements()) {
//				Object object = (Object) values.nextElement();
//				log (key + ": " + object);
//			}
//		}
//		
//	}

}
