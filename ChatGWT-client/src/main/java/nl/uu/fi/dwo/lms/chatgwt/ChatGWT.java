package nl.uu.fi.dwo.lms.chatgwt;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.stanziq.strophe.client.Builder;
import com.stanziq.strophe.client.Connection;
import com.stanziq.strophe.client.Connection.Status;
import com.stanziq.strophe.client.Connection.StatusCallback;
import com.stanziq.strophe.client.Element;
import com.stanziq.strophe.client.Handler;

import nl.uu.fi.dwo.lms.chatgwt.util.Base64;
import nl.uu.fi.dwo.lms.chatgwt.util.MD5;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChatGWT implements EntryPoint {
	
	private static String DOMAIN = "chat-dev.dwo.nl";
	private static String BOSH = "wss://chat-dev.dwo.nl/xmpp-websocket";
	private static Logger LOG = Logger.getLogger(ChatGWT.class.getName());
	
	
	
	class ChatHandler extends Handler<Element> {

		@Override
		public boolean handle(Element element) {
			LOG.info("received " + element);
//			String to = element.getAttribute("to");
			String from = element.getAttribute("from");
			String type = element.getAttribute("type");

			NodeList<com.google.gwt.dom.client.Element> elems = element.getElementsByTagName("body");

			if ((type == null ? "chat" == null : type.equals("chat")) && elems.getLength() > 0) {
				Element body = (Element) elems.getItem(0);
				Label afzender = new Label("From: " + from);
				panel.add(afzender);
				Label message = new Label(body.getText());
				panel.add(message);

				LOG.info("ECHOBOT: I got a message from " + from + ": " + body.getText());
//				String[][] attributes = { { "to", from }, { "from", to }, { "type", "chat" } };
//				Builder reply = Builder.$msg(attributes).cnode(body.copy());
//				connection.send(reply.tree());

//				LOG.info("ECHOBOT: I sent " + from + ": " + body.getText());
			}
			return true;
		}
		
	}
	
	class ChatStatusCallback extends StatusCallback {

		@Override
		public void statusChanged(Status status, String reason) {
			LOG.info("status = " + status + " reason = " + reason);
			
			if (status == Status.CONNECTED) {
				LOG.info("start talking");
				Handler<com.stanziq.strophe.client.Element> handler = new ChatHandler();
				connection.addHandler(null, "message", null, null, null, handler);
				Builder pres = Builder.$pres(null);
	            connection.send(pres);
			}
			
		}
		
	}
	
	
	private Connection connection;
	private TextBox username;
	private TextBox password;
	private VerticalPanel panel;
	private TextBox input;
	private String jid;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		
		username = new TextBox();
		RootPanel.get().add(username);
		password = new PasswordTextBox();
		password.setValue("");
		RootPanel.get().add(password);
		
		Button btn = new Button("LOGIN");
		btn.addClickHandler(this::onClickLogin);
		RootPanel.get().add(btn);
		
		panel = new VerticalPanel();
		RootPanel.get().add(panel);
		
		input = new TextBox();
		input.addClickHandler(this::onClickInput);
		RootPanel.get().add(input);		
		
	}



	private void onClickLogin(ClickEvent event) {
		connection = new Connection(BOSH);
		
		StatusCallback callback = new ChatStatusCallback();
		String u = username.getValue();
		jid = u + "@" + DOMAIN;
		String password = this.password.getValue();
		// password = base64( u + ":" + md5(password))
		password = MD5.md5(password);
		password = u + ":" + password;
		password = Base64.btoa(password);

		connection.connect(jid, password, callback);		
	}

	private void onClickInput(ClickEvent event) {
		String value = input.getValue();
		LOG.info("send " + value);
		
		String[][] attributes = { { "to", "project_wim@" + DOMAIN }, { "from", jid  }, { "type", "chat" } };
		Element body = Element.xmlTextNode(value);
		Builder reply = Builder.$msg(attributes).cnode(body);
		connection.send(reply);
	}
	
	
	
	
}
