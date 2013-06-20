package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.utils.MD5;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MPasswordTextBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;

public class LoginViewImpl implements LoginView, LoginView.Presenter
{
	private LayoutPanel main;
	private Presenter presenter = this ;
	private static final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
	{

		@Override
		public void onFailure(Throwable caught)
		{
			GWT.log("login failure", caught);
			if (caught.getMessage().contains("LoginException"))
				Window.alert("Gebruikersnaam/wachtwoord combinatie niet juist");
			else
				Window.alert("Unable to login");

		}

		@Override
		public void onSuccess(Map<String, Object> result)
		{
			DWOplayer.profiledata = result;
			DWOplayer.clientfactory.getPlaceController().goTo(new ProfilePlace("Profile"));
		}

	};

	HandlerRegistration submit, gast, g2;
	private Button gastbutton;
	private Button submitbutton;
	private MTextBox username;
	private MPasswordTextBox passwd;
	
	public LoginViewImpl()
	{
		main = new LayoutPanel();

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Login");
		main.add(header);

		//create login view
		//LayoutPanel list = new LayoutPanel();
		//main.add(list);

		Label usernamelabel = new Label("Gebruikersnaam:");

		LayoutPanel first = new LayoutPanel();
		first.setWidth("600px");

		Image filogo = new Image("images/resources/filogo.png");
		first.add(filogo);
		Label dwolabel = new Label("Digitale Wiskunde Omgeving");
		dwolabel.getElement().setInnerHTML("<h1>Digitale Wiskunde Omgeving</h1><h4>Freudenthal Instituut</h4>");
		//Label filabel = new Label("Freudenthal Instituut");
		filogo.getElement().getStyle().setFloat(Style.Float.LEFT);
		dwolabel.getElement().getStyle().setFloat(Style.Float.LEFT);
		dwolabel.getElement().getStyle().setMargin(20, Unit.PX);

		first.getElement().getStyle().setDisplay(Display.BLOCK);
		first.getElement().getStyle().setProperty("margin", "20px auto");
		usernamelabel.getElement().getStyle().setProperty("clear", "both");

		usernamelabel.setWidth("300px");
		usernamelabel.getElement().getStyle().setProperty("margin", "10px auto");

		//first.setHeight("140px");
		first.add(dwolabel);
		//first.add(filabel);
		first.add(usernamelabel);
		main.add(first);

		usernamelabel.getElement().addClassName("listitem");
		username = new MTextBox();
		username.getElement().getFirstChildElement().getStyle().setColor("#000");
		username.getElement().getStyle().setBackgroundColor("#eee");
		username.getElement().getFirstChildElement().getStyle().setProperty("background", "none");
		username.getElement().getFirstChildElement().getStyle().setProperty("padding", "12px 12px");
		username.getElement().getFirstChildElement().getStyle().setWidth(274, Unit.PX);

		username.getElement().getStyle().setProperty("border", "1px solid #999");
		username.getElement().getStyle().setProperty("borderRadius", "5px");

		username.setWidth("300px");
		username.getElement().getStyle().setProperty("margin", "auto");

		username.setName("userid");
		first.add(username);

		Label pwlabel = new Label("Wachtwoord:");

		pwlabel.setWidth("300px");
		pwlabel.getElement().getStyle().setProperty("margin", "10px auto");

		first.add(pwlabel);
		pwlabel.getElement().addClassName("listitem");
		passwd = new MPasswordTextBox();
		passwd.setName("passwd");
		passwd.getElement().getFirstChildElement().getStyle().setColor("#000");
		passwd.getElement().getStyle().setBackgroundColor("#eee");
		passwd.getElement().getFirstChildElement().getStyle().setProperty("background", "none");
		passwd.getElement().getFirstChildElement().getStyle().setProperty("padding", "12px 12px");
		passwd.getElement().getFirstChildElement().getStyle().setWidth(274, Unit.PX);

		passwd.getElement().getStyle().setProperty("border", "1px solid #999");
		passwd.getElement().getStyle().setProperty("borderRadius", "5px");

		passwd.setWidth("300px");
		passwd.getElement().getStyle().setProperty("margin", "auto");

		first.add(passwd);

		submitbutton = new Button();
		submitbutton.setText("Login");
		submitbutton.setWidth("276px");
		submitbutton.getElement().getStyle().setProperty("margin", "30px auto");

		first.add(submitbutton);

		gastbutton = new Button() 
		{
			@Override
			public void fireEvent(GwtEvent<?> event) {
				final String string = event.toDebugString();
				GWT.log(string);
				if(string.endsWith("DownEvent:")) {
					GWT.log("break");
				}
				super.fireEvent(event);
			}}
		;
		gastbutton.setText("Login als gast");
		gastbutton.setWidth("276px");
		gastbutton.getElement().getStyle().setProperty("margin", "30px auto");

		first.add(gastbutton);
		
//		g2 = gastbutton.addTouchStartHandler(new TouchStartHandler() {
//			
//			@Override
//			public void onTouchStart(TouchStartEvent event) {
//				GWT.log("touch start");
//			}
//		});
	}

	private void start() {
		submit = submitbutton.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				presenter.login(username.getText(), passwd.getText());
			}
		});
		gast = gastbutton.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				presenter.login();
			}
		});
	}

	public void login(String name, String password)
	{
		RPCHandler handler = DWOplayer.clientfactory.getRPCHandler();
		handler.login(name, password, LOGIN_CALLBACK);
	}
	
	public void login() {
		DWOplayer.gotoCourses();
	}
	

	@Override
	public Widget asWidget()
	{
		return main;
	}

	public void setupModule(Presenter presenter)
	{
		stop();
		this.presenter = presenter;
		if(presenter != null) start();
	}

	private void stop() {
		if(submit != null) submit.removeHandler(); submit=null;
		if(gast != null) gast.removeHandler(); gast = null;
	}
}
