package fi.dwo.server.db;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class DownLoad extends HttpServlet {

	private static class RedirectDispatcher implements RequestDispatcher {

		private final String download;

		public RedirectDispatcher(String download) {
			this.download = download;
		}

		@Override
		public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
			((HttpServletResponse) response).sendRedirect(download);

		}

		@Override
		public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		}

	}

	private String download = "https://www.numworx.nl/help/downloads/";
	private RequestDispatcher dispatch = new RedirectDispatcher(download);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dispatch.forward(req, resp);
	}

	@Override
	public void init() throws ServletException {
		String param;
		param = getInitParameter("fi.dwo.server.db.download");
		if (param != null)
			param = getServletContext().getInitParameter(param);
		if (param != null && !param.isEmpty() && !"${UU_DOWNLOAD}".equals(param)) {
			download = param;
			if (download.startsWith("/"))
				dispatch = getServletContext().getRequestDispatcher(download);
			else
				dispatch = new RedirectDispatcher(param);
		}
	}

}
