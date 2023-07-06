package nl.numworx.osiris.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.csv.CSVRecord;
import org.xml.sax.InputSource;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.numworx.edexml.OsirisBuilder;
import nl.numworx.edexml.ServerBuilder;
import nl.numworx.osiris.Col;
import nl.numworx.osiris.CourseManager;
import nl.numworx.osiris.Excel;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.OAuthManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;


@SuppressWarnings("serial")
@WebServlet(urlPatterns="/upload.html")
@MultipartConfig
public class InstallServlet extends HttpServlet {

	private static final String UTF_8 = "UTF-8";
	private Logger LOG = Logger.getLogger(getClass().getName());

	final static Col TOETSEN[] = {
			Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING
	};

	final public static Col STUDENTEN[] = {
			Col.STUDENTNUMMER, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING
	};

	final static Col DOCENTEN[] = {
			Col.COLLEGEJAAR, Col.CURSUS, Col.LDAP_LOGIN
	};
	final static Col COURSES[] = {
			Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL
	};

	private static final String UU = "\"saml:" + System.getProperty("ENV_ORGID", "385")+"\"";

	public InstallServlet() {
		Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}

	@Override
	protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StoredRestManager instance = StoredRestManager.getInstance();
		ServerBuilder numworx = new ServerBuilder();
		DomLoginContext loginContext = null;
		String upload = "/upload.html";
		try {
			SystemManager system;
			instance.setBasicAuthString(null, null, null);
			instance.getAuthenticator().setServerUrlPath(new URL("http://127.0.0.1/dwo/"));
			system = new SystemManager(instance);
			DomSamlUser user = new DomSamlUser();
			user.setSamlUserId(req.getRemoteUser()); 
														//user.setSamlUserId("staff1"); // DEBUG
			user.setSamlOrgId(UU);
			user = system.requestSamlToken(user);
			
			String samlUserID = user.getSamlUserId();	      
			String samlOrgID = user.getSamlOrgId();
			String authToken = user.getAuthToken();
			log("getToken " + samlUserID + " " + samlOrgID + " " + authToken);
			String token = "3\f" + samlUserID + '\f' + samlOrgID + '\f' + authToken;
			token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
			OAuthManager m = new OAuthManager(instance);
			token = m.authorization_token(token, null, null, null);
			if (token != null) {
			  instance.getAuthenticator().setUsername(user.getSamlUserId()); // for debugging.
			  instance.setRecover(null); // FIXME
			} else {
				upload = "/noway.html";
				log("No token for " + samlUserID + " " + samlOrgID + " " + authToken);
			}
			loginContext = SecureUserAccountManager.getLoginContext();
			numworx.setSource(loginContext.getRealm(), instance);
		} catch (Dwo2Exception e1) {
				log("loginUser", e1);
				upload = "/noway.html";
		} finally {
			try {
				if (loginContext != null) SecureUserAccountManager.logoutUser(loginContext);
			} catch (Dwo2Exception e) {
				log("logoutUser", e);
				throw new ServletException(e.getLocalizedMessage(), e);				
			}
		}
		
		InputStream in = getClass().getResourceAsStream(upload);
		byte buffer[] = new byte[1024];
		int len;
		resp.setContentType("text/html");
		resp.setCharacterEncoding(UTF_8);
		while ( (len = in.read(buffer)) >= 0) resp.getOutputStream().write(buffer, 0, len);
	}

	private void close(InputSource is) {
		try {
			is.getByteStream().close();
		} catch (IOException e) {
		}
		
	}

	@Override
	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		StoredRestManager instance = StoredRestManager.getInstance();
		ServerBuilder numworx = new ServerBuilder();
		DomLoginContext loginContext = null;
		try {
			SystemManager system;
			instance.setBasicAuthString(null, null, null);
			instance.getAuthenticator().setServerUrlPath(new URL("http://localhost/dwo/"));
			system = new SystemManager(instance);
			DomSamlUser user = new DomSamlUser();
			user.setSamlUserId(req.getRemoteUser()); 
														//user.setSamlUserId("staff1"); // DEBUG
			user.setSamlOrgId(UU);
			user = system.requestSamlToken(user);
			
			String samlUserID = user.getSamlUserId();	      
			String samlOrgID = user.getSamlOrgId();
			String authToken = user.getAuthToken();
			String token = "3\f" + samlUserID + '\f' + samlOrgID + '\f' + authToken;
			token = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
			OAuthManager m = new OAuthManager(instance);
			token = m.authorization_token(token, null, null, null);
			if (token != null) {
			  instance.getAuthenticator().setUsername(user.getSamlUserId()); // for debugging.
			  instance.setRecover(null); // FIXME
			} else {
				throw new ServletException("Unauthenticated");
			}
			loginContext = SecureUserAccountManager.getLoginContext();
			numworx.setSource(loginContext.getRealm(), instance);
		} catch (Dwo2Exception e1) {
			throw new ServletException(e1.getLocalizedMessage(), e1);
		}

		
		
		PrintWriter out = resp.getWriter();
		out.print("<h1>Import data</h1>");
		//out.println("User: " + req.getRemoteUser() + ", " + req.getAuthType());
		String message  = "";
		try {
			OsirisBuilder osiris = new OsirisBuilder();
// init osiris from numworx
			Map<String, DomSchoolClassFull> initial = numworx.parseGroepen();
			osiris.setGroepenSource(initial.values());
			int initialSize = initial.size();
						
			InputSource is;
			Iterable<CSVRecord> toetsen = Collections.emptySet();
			Iterable<CSVRecord> courses = Collections.emptySet();
			Part cursus = req.getPart("cursus");
			if (cursus != null) {
				Excel excel = new Excel();
				message = "Reading " + "courses" ;// cursus.getSubmittedFileName();
				InputStream in = cursus.getInputStream();
				excel.parse(in);
				in.close();
				notok(excel.verify(COURSES));
				courses = excel;
				osiris.setGroepenSource(excel);
			}
			Part toets = req.getPart("toets");
			if (toets != null) {				
				Excel excel = new Excel();
				message = "Reading " + "assessments" ;// cursus.getSubmittedFileName();
				InputStream in = toets.getInputStream();
				excel.parse(in);
				in.close();
				notok(excel.verify(TOETSEN));
				toetsen = excel;
				osiris.setGroepenSource(toetsen);
				
			}
			Part student = req.getPart("student");
			Iterable<CSVRecord> studenten = Collections.emptySet();
			if (student != null) {
				Excel excel = new Excel();
				message = "Reading " + "students" ;// cursus.getSubmittedFileName();
				InputStream in = student.getInputStream();
				excel.parse(in);
				in.close();
				notok(excel.verify(STUDENTEN));
								
				studenten = excel;
				osiris.setGroepenSource(studenten);				
				osiris.setLeerlingenSource(studenten);				
			}

			Part docent = req.getPart("docent");
			if (docent != null) {
				Excel excel = new Excel();
				message = "Reading " + "teachers" ;// cursus.getSubmittedFileName();
				InputStream in = docent.getInputStream();
				excel.parse(in);
				in.close();
				notok(excel.verify(DOCENTEN));
				osiris.setLeerkrachtenSource(excel);
			}
			
			out.print("<p>Courses<p>"); 
			for(CSVRecord r: courses) { out.print(r.get(Col.CURSUS));out.print(' '); } 
			out.print("<p>Exams<p>");
			for(CSVRecord r: toetsen) { out.print(r.get(Col.TOETS));out.print(' '); } 
			out.print("<p>Students<p>");
			out.print(osiris.parseLeerlingen().keySet());
			out.print("<p>Teachers<p>");
			out.println(osiris.parseLeerkrachten().keySet());
// From install panel: 
			int toetsSize = 0;
			String profile = "100";
			  message = "";
		      Map<String, DomUserFull> leerlingen = osiris.parseLeerlingen();
		      Map<String, DomSchoolClassFull> groepen = osiris.parseGroepen();
		      Map<String, DomUserFull> leerkrachten = osiris.parseLeerkrachten();
		      Map<String, Collection<String>> members = osiris.memberships();
		      
		      numworx.addSchoolClasses(groepen);
		      int students = numworx.addStudents(leerlingen, members, groepen);
		      message = students + " student(s)\n";
		      message += (groepen.size()-initialSize) + " course(s)\n";
		      int teachers = numworx.addTeachers(leerkrachten, members, groepen);
		      message += teachers + " teacher(s)\n";

		      CourseManager man = new CourseManager(profile, numworx.getSchool(), groepen);
		      man.initTemplate();
			  for (CSVRecord record: toetsen) {
				if (man.createToets(record))
				  toetsSize ++;
			  }
			  for (CSVRecord record: studenten) {
			    if (man.createToets(record))
			      toetsSize ++;
			  }
			  
			  int folders = 0; 
			  for (DomUserFull u: leerkrachten.values()) {
				  if (man.createTeacher(u))
					  folders++;
			  }
			  if (folders > 0) {
				  message += folders + " folder(s)\n";
			  }
			  
			  message += toetsSize + " exam(s)\n";
			
			message += "Installation done";

			
			out.println("<h1>Results</h1><pre>");
			out.println(message);
			
			
		} catch (Exception e) {
			out.print("<p>Something went wrong<p><pre>");
			out.println(message);
			log("wrong install", e);
		}
	}

	private void notok(boolean verify) {
		if (!verify) 
			throw new IllegalArgumentException("Verification failed");
		
	}

	@Override
	public void log(String msg) {
		LOG.warning(msg);
	}

	@Override
	public void log(String message, Throwable t) {
		LOG.log(Level.WARNING, message, t);
	}
	
}
