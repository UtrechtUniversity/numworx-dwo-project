package nl.numworx.notebook.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owlike.genson.Genson;

import nl.numworx.notebook.common.HubInitializer;
import nl.numworx.notebook.common.Resource;
import nl.numworx.notebook.server.rest.Contents;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@SuppressWarnings("serial")
public class HubServlet extends HttpServlet {
	public static final String AUTHORIZATION = "Authorization";

	HubAPI api;

	@Override
	public void init() throws ServletException {
		super.init();
		api = new HubAPI();
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String auth = req.getHeader("Authorization");
		StoredRestManager rest = StoredRestManager.getInstance(); // not a singleton
		rest.setAuthString(auth);
		DomContext context = new DomContext();
		context.setDomHasRole(new DomHasRole()); // no information
		rest.getAuthenticator().setContext(context);
		DomUserFull user;
		try {
			user = SecureUserAccountManager.getAccountData(); // not a static function
		} catch (Dwo2Exception e) {
			log("failure " + auth, e);
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		String name = user.getUserName();
		
		Genson genson = api.genson;
		HubInitializer init = genson.deserialize(req.getInputStream(), HubInitializer.class);
	
		api.startServer(name);
		boolean touch = false;
		String pfx = "";
		if (init.project != null) {
			api.mkdir(name, init.project);
			pfx = init.project + "/";
		}
		if (init.resources != null) {
			for (Resource r: init.resources) {
				if (r.name .equals(init.notebook)) touch = true;
				Contents contents = new Contents();
				contents.path = pfx + r.name;
				contents.content = r.content;
				contents.type = r.type;
				api.create(name, contents.path, contents);
			}
		}
		if (init.notebook != null && !touch) {
			
		}
		
		resp.setContentType(api.APPLICATION_JSON);
		resp.getWriter().print("true");
	}

}
