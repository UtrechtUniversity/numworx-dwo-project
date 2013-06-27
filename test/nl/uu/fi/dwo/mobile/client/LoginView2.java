package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MPasswordTextBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;

public class LoginView2 implements LoginView {

	private LayoutPanel main;

	
	@Override
	public Widget asWidget() {
		
		return main;
	}

	public void setupModule() {
		

	}

	public LoginView2()
	{
		main = new LayoutPanel();
		
		Button btn = new Button("OOPS");
		main.add(btn);
		if(true)
			return;
		

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Login");
		main.add(header);
		Label usernamelabel = new Label("Gebruikersnaam:");

		LayoutPanel first = new LayoutPanel();
		first.setWidth("600px");

		Image filogo = new Image("images/resources/filogo.png");
		first.add(filogo);
		Label dwolabel = new Label("Digitale Wiskunde Omgeving");
		dwolabel.getElement().setInnerHTML("<h1>Digitale Wiskunde Omgeving</h1><h4>Freudenthal Instituut</h4>");
		
		filogo.getElement().getStyle().setFloat(Style.Float.LEFT);
		dwolabel.getElement().getStyle().setFloat(Style.Float.LEFT);
		dwolabel.getElement().getStyle().setMargin(20, Unit.PX);

		first.getElement().getStyle().setDisplay(Display.BLOCK);
		first.getElement().getStyle().setProperty("margin", "20px auto");
		usernamelabel.getElement().getStyle().setProperty("clear", "both");

		usernamelabel.setWidth("300px");
		usernamelabel.getElement().getStyle().setProperty("margin", "10px auto");

		first.add(dwolabel);
		first.add(usernamelabel);
		main.add(first);

		usernamelabel.getElement().addClassName("listitem");
		final MTextBox username = new MTextBox();
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
		final MPasswordTextBox passwd = new MPasswordTextBox();
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

		Button submitbutton = new Button();
		submitbutton.setText("Login");
		submitbutton.setWidth("276px");
		submitbutton.getElement().getStyle().setProperty("margin", "30px auto");
		
		first.add(submitbutton);

		Button gastbutton = new Button();
		gastbutton.setText("Login als gast");
		gastbutton.setWidth("276px");
		gastbutton.getElement().getStyle().setProperty("margin", "30px auto");
		

		first.add(gastbutton);
		
		
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasTapHandlers getLoginBtn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasTapHandlers getGuestBtn() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
