package nl.numworx.notebook.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owlike.genson.Genson;

import nl.numworx.notebook.common.HubInitializer;

@SuppressWarnings("serial")
public class HubServlet extends HttpServlet {
	
	HubAPI api;

	@Override
	public void init() throws ServletException {
		super.init();
		api = new HubAPI();
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Genson genson = api.genson;
		HubInitializer init = genson.deserialize(req.getInputStream(), HubInitializer.class);
	
		resp.setContentType(api.APPLICATION_JSON);
		resp.getWriter().print("true");
	}

}
