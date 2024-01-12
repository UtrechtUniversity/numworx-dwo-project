package fi.dwo.server.db;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RootServlet extends HttpServlet {

	String index = "/index.jsp";
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		RequestDispatcher dispatch = req.getRequestDispatcher(index);
		dispatch.forward(req, resp);
	}

	@Override
	public void init() throws ServletException {
		String param = getServletContext().getInitParameter("fi.dwo.server.db.root");
		if (param != null && !param.isEmpty() && !"${UU_INDEX}".equals(param))
			index = param;
		log ("/ redirected to " + index + " (" + param + ")");
	}

}
