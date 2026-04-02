package nl.uu.fi.dwo.register.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@SuppressWarnings("serial")
public class RegisterForm extends HttpServlet {
	
  private static final String SUBMIT_STUDENT_TO_SCHOOL_CLASS = "submitStudentToSchoolClass";
  private static final String REGISTER_TEAM_LEADER = "registerTeamLeader";
  private static final String DEMO = "DEMO";
  private static final String BRIN = "brin";
  private static final String ORGANIZATION = "organization";
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private static final String WISWISE_FREE = "WISWISE-FREE";
  private static final String ADMINISTRATIE = "administratie@numworx.nl";
  private static final String WIM = "w.vanvelthoven@numworx.nl";
  private final Logger LOG = Logger.getLogger(getClass().getName());
  SystemManager manager;
  Session session;
  private Key key;
  private InternetAddress smtpEmail;
  private RequestDispatcher dispatch;
  private ResourceBundle mailrb;
  private String registerFree;
  private String pfx;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id"); // teamleader id of contact
    String email = req.getParameter("email");
    String givenName = req.getParameter("givenName");
    if (givenName == null) givenName = "";
    String insertion = req.getParameter("insertion");
    if(insertion == null) insertion = "";
    String familyName = req.getParameter("familyName");
    if (familyName == null) familyName = "";
    String server = req.getRequestURL().toString();
    String form = req.getParameter("form");
    String locale = req.getParameter("locale");
    if (locale == null) locale = req.getParameter("language"); // XXX smtpservice nog language
    ResourceBundle mailrb = this.mailrb;
    JwtBuilder claim = Jwts.builder().setIssuer(server)
      .setSubject(email)
      .claim("givenName", givenName)
      .claim("insertion", insertion)
      .claim("familyName", familyName);
    if (locale != null) {
      claim.claim("locale", locale);
      mailrb = ResourceBundle.getBundle("nl.uu.fi.dwo.register.server.mail", Locale.forLanguageTag(locale));
    } else 
      mailrb = this.mailrb;
    if (id != null) {
      claim.claim("id", id);
    }
    String organization="";
// case DEMO
    if (DEMO.equalsIgnoreCase(form)) {
      // organization/brin
      organization = req.getParameter(ORGANIZATION);
      String brin = req.getParameter("Brinnummer"); // name from teamleader
      brin = generateBrin(brin, organization);
      organization += " (" + DEMO +")";
      try { 
    	  DomSchoolFull school = manager.getSchool(brin);
    	  if (school != null && ! organization.equals(school.getSchoolName())) {
    		  mailError(email, req.getParameter(ORGANIZATION));
    		  return;
    	  }
    	  if (school == null) throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ObjectAlreadyExists, brin);
      } catch(Dwo2Exception e) {
    	  DomSchoolFull school = new DomSchoolFull();
    	  school.setAboType(AboType.demo);
    	  Date d = new Date();
    	  d.setDate(d.getDate() + 42);
    	  school.setExpire(d);
    	  school.setExport(Boolean.FALSE);
    	  school.setSchoolLogin(brin);
    	  school.setSchoolName(organization);
    	  school.setSchoolRights("_");
    	  List<DomMapEntry<RoleType, String>> passwords = new ArrayList<DomMapEntry<RoleType,String>>();
    	  String password;
    	  password = "L" + encode(brin);
    	  passwords.add(new DomMapEntry<RoleType, String>(RoleType.STUDENT, password));
    	  password = "D" + encode(password);
    	  passwords.add(new DomMapEntry<RoleType, String>(RoleType.TEACHER, password));
    	  password = "C" + encode(password);
    	  passwords.add(new DomMapEntry<RoleType, String>(RoleType.SCHOOLADMIN, password));    	  
    	  school.setPasswords(passwords);
    	  try {
			manager.submitSchool(school);
		} catch (Dwo2Exception e1) {
  		  mailError(email, req.getParameter(ORGANIZATION));
  		  return;
		}
      }
      claim = claim.claim(BRIN,brin).setId(DEMO).claim("role", RoleType.TEACHER.name());     
    } else if (WISWISE_FREE.equalsIgnoreCase(form)) {
    	claim = claim.setId(WISWISE_FREE)
    			.claim("schoolClass", "Klas Free")
    			.claim("role", RoleType.STUDENT.name())
    			.claim(BRIN,"wiswise");
    }
    String jwt = claim
      .setNotBefore(new Date())
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
    
    String content = "";
    try {
    StringBuffer url = req.getRequestURL();
    url.append("?j=").append(jwt);
    if (locale != null) {
      url.append("&locale=").append(URLEncoder.encode(locale));
    }
    MimeMessage message = new MimeMessage(session);

    //FIXME i18n
    String abo = "Numworx Free";
    if (DEMO.equals(form)) {
    	abo = "Numworx demo-omgeving";
    } else if (WISWISE_FREE.equals(form)) {
    	abo = "Wiswise Free";
    }
    LOG.info("mailing " + abo + " in " + mailrb.getString("language"));
    
    String format;
    try {
      format = mailrb.getString("mail.body."+form); // demo free
    } catch (Exception e) {
      format = mailrb.getString("mail.body.FREE");  // wiswise free
    }
    content += MessageFormat.format(format, url, givenName, insertion, familyName, abo, organization);
    message.setContent(content, mailrb.getString("mail.mime"));
    message.setFrom(smtpEmail);
    message.setSubject(mailrb.getString("mail.subject"));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(ADMINISTRATIE));
    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(WIM));
    Transport.send(message);

    } catch (MessagingException e) {
      LOG.log(Level.SEVERE, "mail error", e);
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if("html".equals(form)||WISWISE_FREE.equals(form)) {
      resp.setContentType(mailrb.getString("mail.mime"));
      resp.getWriter().print(content);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  private void mailError(String email, String org) {
	MimeMessage message = new MimeMessage(session);
	try {
		message.setSubject(org);
		message.setFrom(smtpEmail);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		message.addRecipient(Message.RecipientType.BCC, new InternetAddress(ADMINISTRATIE));
		message.addRecipient(Message.RecipientType.BCC, new InternetAddress(WIM));
		message.setContent("Er is een probleem met de aanmaak van de demo-omgeving\nProbeer het opnieuw", "text/plain");
		Transport.send(message);
	} catch (Exception e) {
		LOG.log(Level.SEVERE, "mail error " + org, e);
	}
	
}

private String generateBrin(String brin, String organization) {
	if (brin == null || brin.isEmpty()) {
		String encode = encode(organization);
		String code = "SH" + encode;
		return code;
	}
	return brin;
}

private String encode(String string) {
	try {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] bytes = digest.digest(string.getBytes(UTF_8));
		String encode = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		return encode;
	} catch (NoSuchAlgorithmException e) {
		return string;
	}
}

@Override
  public void init() throws ServletException {
	manager = new Manager().getInstance(getServletContext());
	this.pfx = getInitParameter("context");
    if (pfx == null) pfx = "";
	// mail parameters
    //place this in servlet
    String smtpServer = initParameter("fi.dwo.server.rest.smtp.server");
    if(smtpServer == null) smtpServer = "localhost";
    String smtpPort = initParameter("fi.dwo.server.rest.smtp.port");
    if (smtpPort == null) smtpPort = "25";
    String smtpTLS = initParameter("fi.dwo.server.rest.smtp.tls");
    if (smtpTLS == null) smtpTLS = "no";
    String smtpSSL = initParameter("fi.dwo.server.rest.smtp.ssl");
    if (smtpSSL == null) smtpSSL = "false";
    String smtpAuth = initParameter("fi.dwo.server.rest.smtp.auth");
    if (smtpAuth == null) smtpAuth = "false";
    final String smtpUser = initParameter("fi.dwo.server.rest.smtp.user");
    final String smtpPassword = initParameter("fi.dwo.server.rest.smtp.password");
    try {
      smtpEmail = 
          new InternetAddress(initParameter("fi.dwo.server.rest.smtp.email"));
    } catch (AddressException e) {
      throw new ServletException("init", e);
    }//from address.
    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.starttls.enable", smtpTLS);
    props.put("mail.smtp.ssl.enable", smtpSSL);
    props.put("mail.smtp.host", smtpServer);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", smtpAuth);
    LOG.info("mail parameters " + props);
    if (smtpAuth.equals("true")) {
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
    } else {
        session = Session.getDefaultInstance(props);
    }
// Key
    byte[] bytes = new byte[32];
    key = Keys.hmacShaKeyFor(bytes);
    registerFree = getInitParameter("register.free");
    LOG.info("register.free = " + registerFree);
    if (registerFree == null) registerFree="/RegisterFree.jsp";
    dispatch = getServletContext().getRequestDispatcher(registerFree);
// I18N
    mailrb = ResourceBundle.getBundle("nl.uu.fi.dwo.register.server.mail", Locale.forLanguageTag("nl"));
    LOG.info("default language " + mailrb.getString("language"));
  }

  private String initParameter(String name) {
	String result = getInitParameter(name);
	if (result == null) {
		result = getServletContext().getInitParameter(name); // fallback to context parameter.
	}
	return result;
}

@Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    
    String server = req.getRequestURL().toString();
    String jwt = req.getParameter("j");
    if (jwt == null) {
    	resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    	return;
    }
    Jws<Claims> claims = Jwts.parser()
  //      .requireIssuer(server)
        .setSigningKey(key).parseClaimsJws(jwt);
    Claims body = claims.getBody();
    String email = body.getSubject();
    String givenName = body.get("givenName", String.class);
    String insertion = body.get("insertion", String.class);
    String familyName = body.get("familyName", String.class);
    String locale = body.get("locale", String.class);
    String tlid = body.get("id", String.class);
    ResourceBundle mailrb = this.mailrb;
    if (locale != null) {
      mailrb = ResourceBundle.getBundle("nl.uu.fi.dwo.register.server.mail", Locale.forLanguageTag(locale));
      if ( !locale.equals(req.getParameter("locale"))) return;
      req.setAttribute("locale", locale);
    } else {
      req.setAttribute("locale", "nl");
    }
    String suggestion = givenName + insertion + familyName;
    suggestion = suggestion.toLowerCase();
    suggestion = suggestion.replaceAll("\\W", "");
    try {
    	if (!suggestion.isEmpty())
    		suggestion = manager.getSuggestion(suggestion);
	} catch (Exception e) {
		
	}
    
    Cookie cookie, putRequest;
    cookie = new Cookie("email", u(email));
    resp.addCookie(cookie);
    cookie = new Cookie("insertion", u(insertion));
    resp.addCookie(cookie);
    cookie = new Cookie("givenName", u(givenName));
    resp.addCookie(cookie);
    cookie = new Cookie("familyName", u(familyName));
    resp.addCookie(cookie);
    cookie = new Cookie("suggestion", u(suggestion));
    resp.addCookie(cookie);
 
    if (tlid != null) {
    	putRequest = new Cookie("putRequest", u(registerTL_jwt(server, email, tlid)));
    } else {
    	putRequest = null;
    }
    cookie = new Cookie("next", u(mailrb.getString("next")));
    resp.addCookie(cookie);
    cookie = new Cookie("cancel", u(mailrb.getString("cancel")));
    resp.addCookie(cookie);
   
    String role = body.get("role", String.class);
    String brin = body.get("brin", String.class);
    String id = body.getId();
    if (id != null) {
    	cookie = new Cookie("schoolGroup", u(role));
    	cookie.setMaxAge(-1);
    	resp.addCookie(cookie);
    	cookie = new Cookie("schoolLogin", u(brin));
    	resp.addCookie(cookie);
    	cookie = new Cookie("form", id);
    	resp.addCookie(cookie);
    	DomSchoolFull school = null;
		try {
			school = manager.getSchool(brin); // Bij DEMO aantal docenten moet 0 zijn.
			List<DomTeacher> teachers = manager.getTeachersInSchool(school);
			if (DEMO.equals(id) && teachers.size() > 0) {
				LOG.warning("has teacher " + teachers.get(0).getUniqueDisplayName());
//				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
				String hetBosIn = mailrb.getString("hetbosin");
			    RequestDispatcher dispatch = getServletContext().getRequestDispatcher(pfx + hetBosIn);			    
			    dispatch.forward(req, resp);
				return;
			}
			if (WISWISE_FREE.equals(id)) {
				String schoolClass = body.get("schoolClass", String.class);
				List<DomSchoolClass> list = manager.getSchoolClasses(school);
				Optional<PersistenceId> classId = 
				list.stream().filter(dsc -> schoolClass.equals(dsc.getSchoolClassName())).map(DomSchoolClass::getId).findAny();
				if (classId.isPresent())
				{ PersistenceId pid = classId.get();
				  putRequest = (new Cookie("putRequest", u(schoolClass_jwt(pid, server, email, tlid))));	
				}
			}
		} catch (Dwo2Exception e) {
			LOG.log(Level.WARNING, "getSchool", e);
			resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
    	RoleType key = RoleType.valueOf(role);
    	String password = 
    	school.getPasswords().stream().filter(item -> item.getKey() == key).findAny().get().getValue();
    	cookie = new Cookie("schoolCode", u(password));
    	resp.addCookie(cookie);
    } else {
      cookie = new Cookie("form", "FREE");
      cookie.setMaxAge(0);
      resp.addCookie(cookie);
    }
    if (putRequest != null) resp.addCookie(putRequest);
    dispatch.forward(req, resp);
  }


private String schoolClass_jwt(PersistenceId pid, String server, String email, String tlid) {
	return Jwts.builder().setIssuer(server)
		      .setSubject(email)
		      .setId(pid.getIdString())
		      .setIssuedAt(new Date())
		      .setAudience(SUBMIT_STUDENT_TO_SCHOOL_CLASS)
		      .claim("id", tlid)
		      .signWith(key, SignatureAlgorithm.HS256)
		      .compact();
}

private String registerTL_jwt(String server, String email, String tlid) {
	return Jwts.builder().setIssuer(server)
			.setSubject(email)
			.setIssuedAt(new Date())
			.setAudience(REGISTER_TEAM_LEADER)
			.claim("id", tlid)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
}



public static String u(String value) {
	if (value != null) {
		try {
			value = URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException ignore) {
		}
	}
	return value;
}

/* (non-Javadoc)
 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String server = req.getRequestURL().toString();
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth == null) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    ServletInputStream in = req.getInputStream();
    InputStreamReader reader = new InputStreamReader(in, UTF_8);
    StringBuilder sb = new StringBuilder();
    int ch;
    while ((ch = reader.read()) >= 0)
      sb.append((char) ch); // readfully.
    String jwt = sb.toString();
    Jws<Claims> claims = Jwts.parser().requireIssuer(server).setSigningKey(key).parseClaimsJws(jwt);
    Claims body = claims.getBody();
    String audience = body.getAudience();
    Manager man = new Manager(getServletContext());
    if (SUBMIT_STUDENT_TO_SCHOOL_CLASS.equals(audience)) {
    try {
      DomUserFull user = man.getUser(auth);
      if (! user.getEmail().equals(body.getSubject()))
        throw new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, user.getUniqueDisplayName());

      DomStudent student = new DomStudent(user);
      DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
      submit.setStudent(student);
      DomSchoolClass schoolClassTo = new DomSchoolClass();
      schoolClassTo.setId(new PersistenceId(body.getId()));
      submit.setSchoolClassTo(schoolClassTo);
      manager.submitStudentToSchoolClass(submit);
      String tlid = body.get("id", String.class);
      register(user.getUserName(), user.getEmail(), tlid);
    } catch (Dwo2Exception e) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    // all ok
    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } else
    if (REGISTER_TEAM_LEADER.equals(audience))
    {
        try {
			DomUserFull user = man.getUser(auth);
			if (! user.getEmail().equals(body.getSubject()))
			  throw new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, user.getUniqueDisplayName());
			String tlid = body.get("id", String.class);
			register(user.getUserName(), user.getEmail(), tlid);
		    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} catch (Dwo2Exception e) {
		      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
    } else 
    {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
  }

private void register(String userName, String email, String tlid) {
	Properties p  = new Properties();
	if (userName != null) p.setProperty("username", userName);
	if (email != null) p.setProperty("email", email);
	if (tlid != null) p.setProperty("id", tlid);
	TeamAccess.sendTeam(session, p, TeamAccess.UPDATE, smtpEmail);
	
}

}
