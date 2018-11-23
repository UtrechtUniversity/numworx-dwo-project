package nl.uu.fi.dwo.register.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Date;
import java.util.Properties;
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
    content += "\nGo to\n";
    content += url.toString();
    content += "\nto complete your registration";
    message.setContent(content, "text/plain");
    message.setFrom(smtpEmail);
    message.setSubject("Registration completion");
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
      resp.setContentType("text/plain");
      resp.getWriter().print(content);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Override
  public void init() throws ServletException {
    String dbrest_url = getInitParameter("dbrest.url");
    if (dbrest_url == null) dbrest_url = "http://localhost/dwo/rest/";
    try {
        RestAuthenticator authenticator = new RestAuthenticator();
        authenticator.setServerUrlPath(new URL(dbrest_url));
        StoredRestManager rest = new StoredRestManager(authenticator);
        manager = new SystemManager(rest);       
    } catch(MalformedURLException ex) {
      throw new ServletException("init", ex);
    }
// mail parameters
    //place this in servlet
    String smtpServer = getInitParameter("fi.dwo.server.rest.smtp.server");
    if(smtpServer == null) smtpServer = "localhost";
    String smtpPort = getInitParameter("fi.dwo.server.rest.smtp.port");
    if (smtpPort == null) smtpPort = "25";
    String smtpTLS = getInitParameter("fi.dwo.server.rest.smtp.tls");
    if (smtpTLS == null) smtpTLS = "no";
    String smtpAuth = getInitParameter("fi.dwo.server.rest.smtp.auth");
    if (smtpAuth == null) smtpAuth = "false";
    final String smtpUser = getInitParameter("fi.dwo.server.rest.smtp.user");
    final String smtpPassword = getInitParameter("fi.dwo.server.rest.smtp.password");
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
    dispatch = getServletContext().getRequestDispatcher("/RegisterFree.html");

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
