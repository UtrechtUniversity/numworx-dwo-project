package fi.dwo.gwt.lib.rest.server;

import fi.dwo.gwt.lib.rest.GWTGlobals;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Handles RestCalls to the DWOServer.
 * 
 * @author Wim van Velthoven
 */
@SuppressWarnings("serial")
public class RestCallHandler extends HttpServlet {

	String dwo2server;
	
	@Override
	public void init() throws ServletException {
		dwo2server = getInitParameter("dwo2server");
		if(dwo2server == null)
			dwo2server = GWTGlobals.instance().getServer();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		super.doGet(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doOptions(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

}
