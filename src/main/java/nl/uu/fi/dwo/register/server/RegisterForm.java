package nl.uu.fi.dwo.register.server;

import java.io.IOException;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

@SuppressWarnings("serial")
public class RegisterForm extends HttpServlet {
	
  private static final String DEMO = "DEMO";
  private static final String BRIN = "brin";
  private static final String ORGANIZATION = "organization";
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private final Logger LOG = Logger.getLogger(getClass().getName());
  SystemManager manager;
  Session session;
  private Key key;
  private InternetAddress smtpEmail;
  private RequestDispatcher dispatch;
  private ResourceBundle mailrb;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String email = req.getParameter("email");
    String givenName = req.getParameter("givenName");
    String insertion = req.getParameter("insertion");
    if(insertion == null) insertion = "";
    String familyName = req.getParameter("familyName");
    String server = req.getRequestURL().toString();
    String form = req.getParameter("form");
    
    JwtBuilder claim = Jwts.builder().setIssuer(server)
      .setSubject(email)
      .claim("givenName", givenName)
      .claim("insertion", insertion)
      .claim("familyName", familyName);
// case DEMO
    if (DEMO.equals(form)) {
      // organization/brin
      String organization = req.getParameter(ORGANIZATION);
      String brin = req.getParameter("Brinnummer"); // name from teamleader
      brin = generateBrin(brin, organization);
      organization += " " + DEMO;
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
    	  school.setExpire(new Date());
    	  school.setExport(Boolean.FALSE);
    	  school.setSchoolLogin(brin);
    	  school.setSchoolName(organization);
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
    }
    String jwt = claim
      .setNotBefore(new Date())
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
    
    String content = "";
    try {
    StringBuffer url = req.getRequestURL();
    url.append("?j=").append(jwt);
    MimeMessage message = new MimeMessage(session);
//FIXME i18n
    String abo = "Numworx Free";
    if (DEMO.equals(form)) {
    	abo = "Numworx demo-omgeving";
    }
    
    
    content += MessageFormat.format(mailrb.getString("mail.body"), url, givenName, insertion, familyName, abo);
    message.setContent(content, mailrb.getString("mail.mime"));
    message.setFrom(smtpEmail);
    message.setSubject(mailrb.getString("mail.subject"));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    Transport.send(message);

    } catch (MessagingException e) {
      LOG.log(Level.SEVERE, "mail error", e);
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if("html".equals(form)) {
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
	// mail parameters
    //place this in servlet
    String smtpServer = initParameter("fi.dwo.server.rest.smtp.server");
    if(smtpServer == null) smtpServer = "localhost";
    String smtpPort = initParameter("fi.dwo.server.rest.smtp.port");
    if (smtpPort == null) smtpPort = "25";
    String smtpTLS = initParameter("fi.dwo.server.rest.smtp.tls");
    if (smtpTLS == null) smtpTLS = "no";
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
// FORM:
    String registerFree = getInitParameter("register.free");
    LOG.info("register.free = " + registerFree);
    if (registerFree == null) registerFree="/RegisterFree.html";
    dispatch = getServletContext().getRequestDispatcher(registerFree);
// I18N
    mailrb = ResourceBundle.getBundle("nl.uu.fi.dwo.register.server.mail");
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
  
    Jws<Claims> claims = Jwts.parser()
        .requireIssuer(server)
        .setSigningKey(key).parseClaimsJws(jwt);
    Claims body = claims.getBody();
    String email = body.getSubject();
    String givenName = body.get("givenName", String.class);
    String insertion = body.get("insertion", String.class);
    String familyName = body.get("familyName", String.class);
    
    String suggestion = givenName + insertion + familyName;
    suggestion = suggestion.toLowerCase();
    suggestion = suggestion.replaceAll("\\W", "");
    try {
		suggestion = manager.getSuggestion(suggestion);
	} catch (Exception e) {
		
	}
    
    Cookie cookie;
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
    
    cookie = new Cookie("next", u(mailrb.getString("next")));
    resp.addCookie(cookie);
    cookie = new Cookie("cancel", u(mailrb.getString("cancel")));
    resp.addCookie(cookie);
   
    String role = body.get("role", String.class);
    String brin = body.get("brin", String.class);
    String id = body.getId();
    if (id != null) {
    	cookie = new Cookie("schoolGroup", u(role));
    	resp.addCookie(cookie);
    	cookie = new Cookie("schoolLogin", u(brin));
    	resp.addCookie(cookie);
    	cookie = new Cookie("form", id);
    	resp.addCookie(cookie);
    	DomSchoolFull school = null;
		try {
			school = manager.getSchool(brin);
		} catch (Dwo2Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	RoleType key = RoleType.valueOf(role);
    	String password = 
    	school.getPasswords().stream().filter(item -> item.getKey() == key).findAny().get().getValue();
    	cookie = new Cookie("schoolCode", u(password));
    	resp.addCookie(cookie);
    }
    dispatch.forward(req, resp);
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
  
}
