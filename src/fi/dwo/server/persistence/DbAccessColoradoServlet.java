package fi.dwo.server.persistence;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DbAccessColoradoServlet extends DbAccessServlet {
	public DbAccessColoradoServlet() {
		super(new DbAccessColorado());
	}

	/* (non-Javadoc)
	 * @see fi.dwo.server.persistence.DbAccessServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO parameters??
		super.init(config);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		out.println(this);
	}


}
