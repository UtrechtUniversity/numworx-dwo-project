package nl.numworx.notebook.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owlike.genson.Genson;

import nl.numworx.notebook.common.HubInitializer;
import nl.numworx.notebook.common.Resource;
import nl.numworx.notebook.server.rest.Contents;
import nl.numworx.notebook.server.rest.Folder;
import nl.numworx.notebook.server.rest.Server;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

@SuppressWarnings("serial")
public class HubServlet extends HttpServlet {
	public static final String AUTHORIZATION = "Authorization";

	HubAPI api;
	StoredRestManager rest;

	@Override
	public void init() throws ServletException {
		super.init();
		if ("true".equals(getInitParameter("debug")))
		Dwo2ExceptionTranslator.setTranslator(new DWO2ExceptionTranslatorInterface() {
			
			@Override
			public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
				return code.name();
			}
			
			@Override
			public String encodeJSON(Dwo2ExceptionCode code, String message) {
				return message;
			}
			
			@Override
			public String decodeMessageInJSON(String json) {
				return json;
			}
			
			@Override
			public Dwo2ExceptionCode decodeCodeInJSON(String json) {
				return Dwo2ExceptionCode.User_IllegalAction;
			}
		});

		api = new HubAPI();
		rest = StoredRestManager.getInstance(); // not a singleton
		try {
			String dbrest_url = getServletContext().getInitParameter("dbrest.url");
			URL path = new URL(dbrest_url);
			rest.getAuthenticator().setServerUrlPath(path);
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String auth = req.getHeader(AUTHORIZATION);
		rest.setAuthString(auth);
		DomContext context = new DomContext();
		context.setDomHasRole(new DomHasRole()); // no information, from path! /dwo/notebook/sec:1-xxx-yyy/create zie upload widget
		rest.getAuthenticator().setContext(context);
		DomUserFull user;
		try {
			user = SecureUserAccountManager.getAccountData(); // not a static function
		} catch (Dwo2Exception e) {
			log("failure " + auth, e);
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
//			user = new DomUserFull(); user.setUserName("project_wim");
		}
		String name = user.getUserName();
		
		Genson genson = api.genson;
		HubInitializer init = genson.deserialize(req.getInputStream(), HubInitializer.class);
	
		Server s = api.startServer(name);
		if (s != null && s.ready != Boolean.TRUE) {
			api.progress(name, t -> log(t.url + " starting " + t.message));
		}
		boolean touch = false;
		String pfx = "";
		if (init.project != null) {
			api.mkdir(name, init.project);
			pfx = init.project + "/";
		}
		Folder ls = null;
		if (init.resources != null) {
			ls = api.listFolder(name, pfx);
			for (Resource r: init.resources) {
				if (r.name .equals(init.notebook)) touch = true;
				Contents contents = new Contents();
				contents.path = pfx + r.name;
				contents.name = r.name;
				contents.content = r.content;
				contents.type = "file";
				contents.format = r.type; // text/base64
				if (!find(ls, r.name))
					api.create(name, contents.path, contents);
			}
		}
		if (init.notebook != null && !touch) {
			if (ls == null) ls = api.listFolder(name, pfx);
			if (! find(ls, init.notebook)) {
				Contents contents = new Contents();
				contents.path = pfx +  init.notebook;
				contents.name = init.notebook;
				contents.type = "file";
				contents.format = "text";
				contents.content = untitled(); // from resource
				api.create(name, contents.path, contents);
			}
		}
		
		resp.setContentType(HubAPI.APPLICATION_JSON);
		resp.getWriter().print("true");
	}

	private String untitled;
	private String untitled() throws IOException {
		if (untitled == null) {
			InputStream in = getClass().getResourceAsStream("resources/Untitled.ipynb");
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
			int avail = in.available(); if (avail > 1024) avail = 1024;
			char[] buffer = new char[avail];
			StringBuilder sb = new StringBuilder();
			int size; while ( (size = reader.read(buffer)) > 0) sb.append(buffer, 0, size);
			reader.close();
			untitled = sb.toString();
		}
		return untitled;
	}

	private boolean find(Folder ls, String name) {
		for (nl.numworx.notebook.server.rest.Resource r: ls.content) {
			if (r.name .equals(name)) return true;
		}
		return false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_NO_CONTENT);
	}

}
