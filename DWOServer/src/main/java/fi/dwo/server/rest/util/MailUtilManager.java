package fi.dwo.server.rest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import org.stringtemplate.v4.ST;

public class MailUtilManager {

	private MailUtilManager() {
	}

	public static void send(String email, Map<String, Object> properties, String resource, String language, ServletContext servletContext) throws IOException, AddressException, MessagingException {
		resource = "/" + resource.replace('.', '/');
        InputStream in = MailUtilManager.class.getResourceAsStream(resource + "_" + language + ".txt");
        if (in == null) in = MailUtilManager.class.getResourceAsStream(resource + ".txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String message;        
        message = "";
        String line;
        while( (line = reader.readLine()) != null) {
        	message += line;
        	message += "\r\n";            }
        reader.close();
        ST template;
        template = new ST(message, '{', '}');
        // niet automatisch, zoals beloofd
        template.groupThatCreatedThisInstance.registerRenderer(String.class, new org.stringtemplate.v4.StringRenderer());        
        template.add("origin", Origin.ORIGINS[0]);  // XXXX NIET GOED, gebruik URIInfo, is dat zo?
        String cdn = System.getProperty("CDNURL", "https://cdn.dwo.nl");
        template.add("cdn", cdn);
        properties.forEach( (k, v) -> template.add(k, v));
        message = template.render(new Locale(language));
        // sending:
        //place this in servlet
        String smtpServer = servletContext.getInitParameter("fi.dwo.server.rest.smtp.server");
        String smtpPort = servletContext.getInitParameter("fi.dwo.server.rest.smtp.port");
        String smtpTLS = servletContext.getInitParameter("fi.dwo.server.rest.smtp.tls");
        String smtpSSL = servletContext.getInitParameter("fi.dwo.server.rest.smtp.ssl");
        String smtpAuth = servletContext.getInitParameter("fi.dwo.server.rest.smtp.auth");
        String smtpUser = servletContext.getInitParameter("fi.dwo.server.rest.smtp.user");
        String smtpPassword = servletContext.getInitParameter("fi.dwo.server.rest.smtp.password");
        String smtpEmail = servletContext.getInitParameter("fi.dwo.server.rest.smtp.email");//from address.
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", smtpTLS);
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", smtpAuth);
        if (smtpSSL != null) props.put("mail.smtp.ssl.enable", smtpSSL);
        Session session;
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
        // uncomment for debugging infos to stdout
        session.setDebug(true);
        Transport transport = session.getTransport();
        MimeMessage mime = new MimeMessage(session);
        mime.setFrom(new InternetAddress(smtpEmail));
//FIXME i18n
        int sep = message.indexOf("\n\n");
        
        String content = message.substring(sep+2);
        message = message.substring(0, sep);

        for (String header : message.split("\n")) mime.addHeaderLine(header);
        
        sep = content.indexOf("----------");
        String text = content.substring(0,sep).trim();
        String html = content.substring(sep+10).trim();

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(text, "utf-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=utf-8");

        MimeMultipart multiPart = new MimeMultipart("alternative");
		multiPart.addBodyPart(textPart); // <-- first
        multiPart.addBodyPart(htmlPart); // <-- second
        mime.setContent(multiPart);            
        
        
        mime.addRecipient(Message.RecipientType.TO,
                new InternetAddress(email));

        transport.connect();
        transport.sendMessage(mime,
                mime.getRecipients(Message.RecipientType.TO));
        transport.close();		
	}

}
