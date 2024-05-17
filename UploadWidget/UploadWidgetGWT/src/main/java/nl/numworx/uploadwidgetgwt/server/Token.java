package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.OAuthManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.oauth.ErrorResponse;

public class Token extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = req.getParameter("refresh_token");
		resp.setContentType("application/json");
		
		StoredRestManager manager = StoredRestManager.getInstance().duplicate();
		OAuthManager oa = new OAuthManager(manager);
		String refresh = oa.refresh_token(token);
		if (refresh == null) {
			ErrorResponse error = new ErrorResponse("invalid_grant");
			resp.setStatus(400);
			manager.getGenson().serialize(error, resp.getOutputStream());
			return;
		}
		
		String auth = manager.getBasicAuthString();
		DomToken dom = new DomToken();
		dom.setRefresh_token(refresh);
		dom.setToken_type(DomToken.BEARER);
		if (auth != null) dom.setAccess_token(auth.substring(7));
		dom.setExpires_in(60);
		manager.getGenson().serialize(dom, resp.getOutputStream());
	
	}

}
