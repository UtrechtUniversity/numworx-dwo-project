package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;

public class Download extends HttpServlet {
	
	Store store;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURL().toString();
		String authorization = (String) req.getSession().getAttribute(Constants.AUTHORIZATION);
		resp.setContentType("text/plain");
		resp.getWriter().println(path);
		Optional<AtomEntry> x = store.findByURL(path);
		
		if (x.isPresent()) {
			AtomEntry entry = x.get();
			resp.getWriter().println(entry.type);
			resp.getWriter().println(entry.length);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURL().toString();
		store.deleteByURL(path);
	}

	@Override
	public void init() throws ServletException {
		store = Store.instance();
	}
	
}
