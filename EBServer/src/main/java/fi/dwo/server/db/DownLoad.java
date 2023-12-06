package fi.dwo.server.db;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DownLoad extends HttpServlet {

	private String download = "https://www.numworx.nl/help/downloads/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(download);
	}

	@Override
	public void init() throws ServletException {
		String param;
		param = getInitParameter("fi.dwo.server.db.download");
		if (param != null)
			param = getServletContext().getInitParameter(param);
		if (param != null) {
			download = param;
		}
	}

}
