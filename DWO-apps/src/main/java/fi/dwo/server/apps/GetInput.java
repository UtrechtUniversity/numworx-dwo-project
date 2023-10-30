package fi.dwo.server.apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GetInput extends HttpServlet {

	private static final String GET_INPUT = "fi.dwo.server.apps.GetInput";

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = getKey(req);
		HttpSession session = req.getSession();
		session.removeAttribute(key);	
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = getKey(req);
		HttpSession session = req.getSession();
		if (session == null) {
			waitput();
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Object result = session.getAttribute(key);
		if (result == null) {
			waitput();
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		resp.setContentType("text/plain");
		resp.getWriter().print(result);
	}

	private synchronized void waitput() {
		try {
			wait(2000L);
		} catch(Exception oops) { }
		
	}

	private String getKey(HttpServletRequest req) {
		return GET_INPUT + Objects.toString(req.getPathInfo(), "/");
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = getKey(req);
		HttpSession session = req.getSession();
		BufferedReader reader = req.getReader();
		Object data = reader.readLine();
		session.setAttribute(key, data);
		synchronized(this) {
			this.notifyAll();
		}
		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}


}
