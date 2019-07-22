package nl.uu.fi.dwo.register.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamAccess extends HttpServlet {
	private static final String WIM = "w.vanvelthoven@numworx.nl";
	public static final String UPDATE = "contacts-update@register.dwo.nl";
	private final static Logger LOG = Logger.getLogger(TeamAccess.class.getName());
	private Session session;
	private InternetAddress smtpEmail;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Enumeration<String> names = req.getParameterNames();
		Properties p = new Properties();
		while (names.hasMoreElements()) {
			String name =  names.nextElement();
			p.setProperty(name, req.getParameter(name));
		}
		sendTeam(session, p, UPDATE, smtpEmail);
		
		resp.sendError(HttpServletResponse.SC_NO_CONTENT);
	}

	private String initParameter(String name) {
		String result = getInitParameter(name);
		if (result == null) {
			result = getServletContext().getInitParameter(name); // fallback to context parameter.
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		String smtpServer = initParameter("fi.dwo.server.rest.smtp.server");
		if (smtpServer == null)
			smtpServer = "localhost";
		String smtpPort = initParameter("fi.dwo.server.rest.smtp.port");
		if (smtpPort == null)
			smtpPort = "25";
		String smtpTLS = initParameter("fi.dwo.server.rest.smtp.tls");
		if (smtpTLS == null)
			smtpTLS = "no";
		String smtpSSL = initParameter("fi.dwo.server.rest.smtp.ssl");
		if (smtpSSL == null)
			smtpSSL = "false";
		String smtpAuth = initParameter("fi.dwo.server.rest.smtp.auth");
		if (smtpAuth == null)
			smtpAuth = "false";
		final String smtpUser = initParameter("fi.dwo.server.rest.smtp.user");
		final String smtpPassword = initParameter("fi.dwo.server.rest.smtp.password");
		try {
			smtpEmail = new InternetAddress(initParameter("fi.dwo.server.rest.smtp.email"));
		} catch (AddressException e) {
			throw new ServletException("init", e);
		} // from address.
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.starttls.enable", smtpTLS);
		props.put("mail.smtp.ssl.enable", smtpSSL);
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.auth", smtpAuth);
		props.put("mail.debug", "true");
		LOG.info("mail parameters " + props);
		if (smtpAuth.equals("true")) {
			session = Session.getInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			});
		} else {
			session = Session.getDefaultInstance(props);
		}
	}

	public static void sendTeam(Session session, Properties props, String recipient, InternetAddress smtpEmail) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<table><tr><td>");
		sb.append("<table>");
		Enumeration<?> names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			insert(sb, name, props.getProperty(name));			
		}
		sb.append("</table>").append("</td></tr></table>\n");
		String body = sb.toString();
		MimeMessage message = new MimeMessage(session);
		try {
			message.setSubject("update contact");
			message.setFrom(smtpEmail);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(WIM));
			message.setContent(body, "text/html");
			Transport.send(message);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "mail error " + recipient, e);
		}
		
	}

	private static void insert(StringBuilder sb, String name, String property) {
		sb.append("<tr><td>").append(name).append("</td></tr>\n");
		sb.append("<tr><td>").append(esc(property)).append("</td></tr>\n");
		
	}

	private static String esc(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;");
	}
}
