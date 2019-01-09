package nl.uu.fi.dwo.register.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

@SuppressWarnings("serial")
public class RegisterForm extends HttpServlet {
	
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
    String familyName = req.getParameter("familyName");
    String server = req.getRequestURL().toString();
    String form = req.getParameter("form");
    
    String jwt = Jwts.builder().setIssuer(server)
      .setSubject(email)
      .claim("givenName", givenName)
      .claim("insertion", insertion)
      .claim("familyName", familyName)
      .setNotBefore(new Date())
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();

    Transport transport;
    String content = "";
    try {
      transport = session.getTransport();

    StringBuffer url = req.getRequestURL();
    url.append("?j=").append(jwt);
    MimeMessage message = new MimeMessage(session);
//FIXME i18n         
    content += MessageFormat.format(mailrb.getString("mail.body"), url);
    message.setContent(content, mailrb.getString("mail.mime"));
    message.setFrom(smtpEmail);
    message.setSubject(mailrb.getString("mail.subject"));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

    transport.connect();
    transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
    transport.close();

    } catch (MessagingException e) {
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
          new InternetAddress(getInitParameter("fi.dwo.server.rest.smtp.email"));
    } catch (AddressException e) {
      throw new ServletException("init", e);
    }//from address.
    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.starttls.enable", smtpTLS);
    props.put("mail.smtp.host", smtpServer);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", smtpAuth);

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
    log("register.free = " + registerFree);
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
	return null;
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
    cookie = new Cookie("email", email);
    resp.addCookie(cookie);
    cookie = new Cookie("insertion", insertion);
    resp.addCookie(cookie);
    cookie = new Cookie("givenName", givenName);
    resp.addCookie(cookie);
    cookie = new Cookie("familyName", familyName);
    resp.addCookie(cookie);
    cookie = new Cookie("suggestion", suggestion);
    resp.addCookie(cookie);

  
    dispatch.forward(req, resp);

  }
  
}
