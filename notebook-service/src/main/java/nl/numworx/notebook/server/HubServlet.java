package nl.numworx.notebook.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.owlike.genson.Genson;

import nl.numworx.notebook.common.HubInitializer;
import nl.numworx.notebook.common.Resource;
import nl.numworx.notebook.server.rest.Contents;
import nl.numworx.notebook.server.rest.Folder;
import nl.numworx.notebook.server.rest.Server;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
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
		String token = System.getProperty(HubAPI.DWO_HUB_TOKEN);
		log("intialize hub api token" + token);
		if (token == null) throw new ServletException("dwo hub token not found");
		api = new HubAPI();
		rest = new StoredRestManager(new RestAuthenticator()); // not a singleton
		rest = StoredRestManager.getInstance();
		try {
			String dbrest_url = getServletContext().getInitParameter("dbrest.url");
			URL path = new URL(dbrest_url);
			rest.getAuthenticator().setServerUrlPath(path);
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	private String getURL(HubInitializer init, String learnerName) {
		String hub = "/";
		String tail = "";
		String notebook = init.notebook;
		String project = init.project;
		LessonMode mode = init.mode;
		if (notebook != null) {
			boolean isnb = notebook.endsWith(".ipynb");
			tail = notebook;
			if (project != null) {
				tail = project + "/" + notebook;
			}
			while(tail.startsWith("/")) tail = tail.substring(1);
			String user = api.encodePathSegment(learnerName);
			hub  += "user/" + user + "/";
			if (mode == LessonMode.browse)
				hub += isnb ? "nbconvert/html/": "files/"; // browsemode textfiles
			else
				hub  += isnb? "doc/tree/": "edit/"; 
			hub += api.encodePathSegment(tail);	
		} else if (project != null) {
			while(project.startsWith("/")) project = project.substring(1);
			String user = api.encodePathSegment(learnerName);
			hub  += "user/" + user + "/";
			hub  += "lab/tree/" + api.encodePathSegment(project);
		}
		
		return api.hubAPI.resolve(hub).toASCIIString();
		
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String auth = req.getHeader(AUTHORIZATION);
		rest.setAuthString(auth);
		DomContext context = new DomContext();
		context.setDomHasRole(new DomHasRole()); // no information, from path! /dwo/notebook/sec:1-xxx-yyy/create zie upload widget
		rest.getAuthenticator().setContext(context);
		DomUserFull user;		
		DomSchoolsRolesAndClassesV2 logins;
		try {
			String code = SecureUserAccountManager.getBearerToken(rest);
			HttpSession session = req.getSession();
			session.setAttribute("dwologin.code", code);
			user = SecureUserAccountManager.getAccountData(rest); // not a static function
		} catch (Dwo2Exception e) {
			log("failure " + auth, e);
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
//			user = new DomUserFull(); user.setUserName("project_wim");
		}
		String name = user.getUserName();
		String info = req.getPathInfo();
		Genson genson = api.genson;
		HubInitializer init = genson.deserialize(req.getInputStream(), HubInitializer.class);

		if (info != null && info.contains("/sec:2") && init.mode == LessonMode.review) {
			try {
				logins = SecureUserAccountLoginsManager.getSchoolLogins();
				int index = info.indexOf("/sec:");
				String[] split = info.substring(index+5).split("/");
				log("found  " + split[0]);
				String pathid=split[0];
				DomHasRole hasRole = logins.getActiveSchoolRoleAndClass().getHasRole();
				context.setDomHasRole(hasRole);
				split = pathid.split("-");
				final String uid = "MYSQL;PersistentUser;"+split[1];
				final String scid = "MYSQL;PersistentSchoolClass;" + split[2];
				List<DomStudent> students = SecureTeacherSchoolClassManager.getTeachersStudents(rest);
				Optional<DomStudent> b1 = students.stream().filter(s -> s.getId().toString().equals(uid)).findAny();
				List<DomSchoolClass> classes = SecureTeacherSchoolClassManager.getTeachersSchoolClasses(rest);
				Optional<DomSchoolClass> b2 = classes.stream().filter(c -> c.getId().getIdString().equals(scid)).findAny();
				if (b1.isPresent() && b2.isPresent()) {
					name = b1.get().getUserName();				
					String code = SecureTeacherSchoolClassManager.getBearerToken(b1.get(),rest);
					HttpSession session = req.getSession();
					session.setAttribute("dwologin.code", code);
 				}
			} catch (Dwo2Exception e) {
				log("failure " + auth, e);
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			
			}

			
			
			
		}
	
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
		PrintWriter out = resp.getWriter();
		out.print('"');out.print(getURL(init, name));out.print('"');
		
	}

	private String untitled;
	private String untitled() throws IOException {
		if (untitled == null) {
			InputStream in = getClass().getResourceAsStream("resources/Untitled.ipynb");
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
			int avail = in.available(); if (avail > 1024) avail = 1024; else if (avail < 128) avail = 128; // minmax
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
