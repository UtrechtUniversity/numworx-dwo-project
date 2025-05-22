package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.OAuthManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

@SuppressWarnings("serial")
public class JavaUpload extends HttpServlet implements Constants {
	
	public static final Logger LOG = Logger.getLogger(JavaUpload.class.getName());
	
	Store store;
	RestAuthenticator authenticator = StoredRestManager.getInstance().getAuthenticator(); // XXX Singleton.
	
	String feed = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
			"<feed xmlns=\"http://www.w3.org/2005/Atom\">\n" + 
			"\n" + 
			"  <title>Upload Feed</title>\n" + 
			"  <link href=\"http://example.org/\"/>\n" + 
			"  <updated>2021-11-26T18:30:02Z</updated>\n" + 
			"  <author>\n" + 
			"    <name>Student</name>\n" + 
			"  </author>\n" + 
			"  <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" + 
			"\n" ;
	String entry = 
			"  <entry>\n" + 
			"    <title>haak.png</title>\n" + 
			"    <link href=\"http://localhost:8888/haak.png\" type=\"image/png\" length=\"1024\" />\n" + 
			"    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" + 
			"    <updated>2003-12-13T18:30:02Z</updated>\n" + 
			"    <summary>Omschrijving hier</summary>\n" + 
			"  </entry>\n" ;
	String tail = 
			"\n" + 
			"</feed>\n" + 
			"";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String bearer = req.getHeader(AUTHORIZATION);
		if (bearer == null) {
			
			bearer = (String) req.getSession().getAttribute(AUTHORIZATION);
			if (bearer == null) {
				LOG.severe( "No bearer from session, HELP!!!!");
			}
			
		}
		else {
			req.getSession().setAttribute(AUTHORIZATION, bearer);
			req.getSession().removeAttribute(TOKEN_RETRY);
		}
		String path = req.getPathInfo();
		int index = path.indexOf("/sec:");
		String paths[] = path.substring(index+5).split("/");
		Optional<DomSchoolRoleAndClassV2> actor = getActor(bearer, paths[0], req.getSession());

		if (!actor.isPresent()) {
			LOG.severe("Actor not present " + bearer + " " + paths[0]);
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
			
		}
		DomSchool school = actor.get().getSchool();		
		String prefix = getPrefix(paths, school);
		
		if (paths.length == 3) {
			StringBuffer url = req.getRequestURL();
			URI u = URI.create(url.toString());
			resp.setContentType("application/atom+xml");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter out = resp.getWriter();
			out.print(feed);
			for (AtomEntry entry: store.getEntries(prefix)) {
				out.println("<entry>");
				out.print(" <title>");out.print(entry.title);out.println("</title>");
				out.print(" <link href='");out.print(u.resolve(entry.url).toString());out.print("' type='");out.print(entry.type);out.print("' length='");out.print(entry.length);out.println("' />");
				out.print(" <id>urn:uuid:");out.print(entry.id);out.println("</id>");
				out.println("</entry>");
			}
			out.print(tail);
			return;
		} 
		if (paths.length == 4) {
			String registration = paths[2];
			boolean instance = "instance".equals(registration);
			log(instance + " find " + prefix + paths[3]);			
			Optional<AtomEntry> found = store.findByURL(prefix + paths[3]);
			log("result " + found);
// bij instance andere logica, groter publiek		
			if ( (instance && found.isPresent()) || store.ownedBy(found, actor)) {
				store.write(found.get(), resp);
				return;
			}
 		} 
		log("NOT FOUND: " + prefix + paths[3] + " actor " + actor );
		
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String getPrefix(String[] paths, DomSchoolId school) {
		String registration = paths[2];
		String uuid = paths[1].replace('-', '/');
		if ("instance".equals(registration)) {
			SystemManager manager = new SystemManager(StoredRestManager.getInstance());
			String[] split = uuid.split("/");
			if ("course".equals(split[0])) {
				DomCourse course = new DomCourse(PersistentCourse.buildPersistenceId(Long.valueOf(split[1])));
				try {
					if (null == manager.getSchool(course))
						school = null;
				} catch (Dwo2Exception e) {
					log("doPut getSchool", e);
				}
			} else {
				DomScoContextId context = new DomScoContextId();
				PersistenceId id = PersistentScoContext.buildPersistenceId(Long.valueOf(split[0]));
				context.setId(id);
				try {
					if (null == manager.getSchool(context))
						school = null;
				} catch (Dwo2Exception e) {
					log("doPut getSchool", e);
				}
			}
		}
		
		String pid = school == null ? "standard" : school.getId().toString();
		return pid + "/" + uuid + "/" + registration + "/";
	}

	public static final String PREFIX = "nl.numworx.oauth2client.server.Oauth2Filter.";
	public static final String PREFIX_TOKEN = PREFIX + "token.";
    public static final String TOKEN_RETRY = PREFIX_TOKEN + "retry";
	
	static class Retry implements Predicate<Dwo2Exception> {
		HttpSession storage;
		StoredRestManager rest;
		private final OAuthManager m;
		@Override
		public boolean test(Dwo2Exception t) {
		    if (t.getDwo2Code() != Dwo2ExceptionCode.User_AuthenticationError) return false;
			String token = (String) storage.getAttribute(TOKEN_RETRY);
		    if (token == null) return false;
		    token = m.refresh_token(token);
		    if (token == null) {
		      t = new Dwo2Exception(Dwo2ExceptionCode.Rest_LoginNeeded, "invalid_grant");
		      storage.removeAttribute(TOKEN_RETRY);
		      throw new RuntimeException(t);
		    }
		    storage.setAttribute(TOKEN_RETRY, token);
		    return true;
		}
		public Retry(HttpSession storage, StoredRestManager rest) {
			super();
			this.storage = storage;
			this.rest = rest;
			m = new OAuthManager(rest);
			Object token = storage.getAttribute(TOKEN_RETRY);
			if (token == null) {
	            try {
					token = m.authorization_token(SecureUserAccountManager.getBearerToken(rest), null, null, null);
				    storage.setAttribute(TOKEN_RETRY, token);
				} catch (Dwo2Exception e) {
				}
			}
		}		
	}
    
	
	
	
	static Optional<DomSchoolRoleAndClassV2> getActor(String bearer, String pathid, HttpSession session) {
		StoredRestManager rest = StoredRestManager.getInstance().duplicate(); // Should not be a singleton!
		DomContext context = new DomContext();
		context.setRealm(null);
		context.setDomHasRole(new DomHasRole());
		rest.getAuthenticator().setContext(context);
// type is ignore case,
		if (bearer != null && bearer.toLowerCase().startsWith("bearer"))
		{
			rest.setBearerAuthString(bearer.substring(6));
		} else if (bearer != null && bearer.toLowerCase().startsWith("basic")) {
			bearer = new String(Base64.getDecoder().decode(bearer.substring(6)));
			String[] split = bearer.split(":", 2);
			rest.setBasicAuthString(split[0], split[1], null);
		} else {
			LOG.severe("unrecognized bearer [" + bearer + "]");
			return Optional.empty();
		}
		rest.setRecover(new Retry(session, rest));
		DomUserFull user;
		DomSchoolsRolesAndClassesV2 logins;
		DomHasRole hasRole;
		try {
			user   = SecureUserAccountManager.getAccountData(rest);
LOG.info("user = " + user.getUniqueDisplayName());
			logins = SecureUserAccountLoginsManager.getSchoolLogins(rest);
LOG.info("logins = " + logins.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
		//search paths[0] in logins for school;
		hasRole = logins.getActiveSchoolRoleAndClass().getHasRole();
		String current = Store.getPathId(hasRole);
		if (current.equals(pathid) && user.getId().equals(hasRole.getUserId()))
		{
			context.setDomHasRole(hasRole);
			return Optional.of(logins.getActiveSchoolRoleAndClass());
		} else {
			if (pathid.startsWith("2-")) {
LOG.info("searching ");
				context.setDomHasRole(hasRole);
				String [] split = pathid.split("-");
				split[1] = "MYSQL;PersistentUser;"+split[1];
				split[2] = "MYSQL;PersistentSchoolClass;" + split[2];
				List<DomStudent> students = SecureTeacherSchoolClassManager.getTeachersStudents(rest);
LOG.info("found students " + students);
				Optional<DomStudent> b1 = students.stream().filter(s -> s.getId().toString().equals(split[1])).findAny();
LOG.info("student " + b1);
				List<DomSchoolClass> classes = SecureTeacherSchoolClassManager.getTeachersSchoolClasses(rest);
LOG.info("classes " + classes);
				Optional<DomSchoolClass> b2 = classes.stream().filter(c -> c.getId().getIdString().equals(split[2])).findAny();
LOG.info("class " + b2);
				if (b1.isPresent() && b2.isPresent()) {
					DomSchoolRoleAndClassV2 result = logins.getActiveSchoolRoleAndClass();
					result.getHasRole().setId(null);
					result.getHasRole().setSchoolGroupId(null);
					result.getHasRole().setUserId(b1.get().getId());
					result.setSchoolClass(b2.get());
					return Optional.of(result);
				}
			} else if (pathid.startsWith("1-") && !user.getSingleSchool()) {
	LOG.info("search hasrole " + pathid);
				return logins.getSchoolsRolesAndClassesList()
				.stream()
				.filter(hrc -> {
					DomHasRole hr = hrc.getHasRole();
					return pathid .equals( Store.getPathId(hr));
				})
				.findAny();
			}
		}
		} catch (Dwo2Exception e) {
			LOG.log(Level.SEVERE, e.toString(), e);
			return Optional.empty();
		}
		LOG.severe("reached empty exit");
		return Optional.empty();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	// sec:1-xxx-yyyy/uuid/registration/file
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		log("doPut: " + req.getPathInfo());

		String bearer = req.getHeader(AUTHORIZATION);
		if (bearer == null) bearer = (String) req.getSession().getAttribute(AUTHORIZATION);
		else req.getSession().setAttribute(AUTHORIZATION, bearer);
		String path = req.getPathInfo();
		int index = path.indexOf("/sec:");
		String paths[] = path.substring(index+5).split("/");
		Optional<DomSchoolRoleAndClassV2> actor = getActor(bearer, paths[0], req.getSession());

		if (!actor.isPresent()) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;			
		}
		DomSchool school = actor.get().getSchool();
		String name = paths[3];
		AtomEntry entry = new AtomEntry();
		entry.title = name;
		entry.type  = req.getContentType();
		entry.length = (long) req.getContentLength(); // req.getContentLengthLong() niet in tomcat7
		StringBuffer requestURL = new StringBuffer();		
		requestURL.append(getPrefix(paths, school))
			.append(entry.title);
		entry.url = requestURL.toString();
		Map<String, String> map = Collections.singletonMap("learnerid", paths[0]);
		store.addEntry(entry, map, req.getInputStream());
		req.getInputStream().close();
		
		resp.setStatus(HttpServletResponse.SC_CREATED);
	}

	@Override
	public void init() throws ServletException {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
		store = Store.instance();
		String dbrest_url = getServletContext().getInitParameter("dbrest.url");
	    try {
			authenticator.setServerUrlPath(new URL(dbrest_url));
		} catch (MalformedURLException e) {
			log("rest parameter incorrect", e);
			throw new ServletException("rest parameter " + dbrest_url, e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bearer = req.getHeader(AUTHORIZATION);
		String path = req.getPathInfo();
		int index = path.indexOf("/sec:");
		String paths[] = path.substring(index+5).split("/");
		if (paths.length == 4) {
			Optional<DomSchoolRoleAndClassV2> actor = getActor(bearer, paths[0], req.getSession());
			if (actor.isPresent()) {
				DomSchool school = actor.get().getSchool();
				String url = getPrefix(paths, school) + paths[3];
				Optional<AtomEntry> item = store.findByURL(url);
				if (store.ownedBy(item, actor)) {
					store.deleteByURL(url);
					resp.sendError(HttpServletResponse.SC_NO_CONTENT);
					return;					
				}
			} else {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
 		}
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

}
