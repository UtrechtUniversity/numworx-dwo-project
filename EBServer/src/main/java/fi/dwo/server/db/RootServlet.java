package fi.dwo.server.db;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RootServlet extends HttpServlet {

	String index = "/index.jsp";
	Map<String, String> roots = new HashMap<>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String host = req.getHeader("Host");
		String index = roots.getOrDefault(host, this.index);
		RequestDispatcher dispatch = req.getRequestDispatcher(index);
		dispatch.forward(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// FIXME: iets met 'getInitParameter'?
		roots.put("cti.dwo.nl", "/cti/index.jsp");
		roots.put("app.co-teach.nl", "/cti/index.jsp");
		roots.put("test.co-teach.nl", "/cti/index.jsp");
		
		String param = getServletContext().getInitParameter("fi.dwo.server.db.root");
		if (param != null && !param.isEmpty() && !"${UU_INDEX}".equals(param))
		{
			index = param;
			roots.put("numworx.acc.uu.nl", param);
			roots.put("numworx.uu.nl", param);
		}
		log ("/ redirected to " + index + " (" + param + ")");
	}

}
