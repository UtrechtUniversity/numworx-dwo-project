/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.BUILD;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * @author wim
 *
 */
public class Login3ViewImpl extends Composite implements LoginView  {

	private static Login3ViewImplUiBinder uiBinder = GWT
			.create(Login3ViewImplUiBinder.class);

	interface Login3ViewImplUiBinder extends UiBinder<Widget, Login3ViewImpl> {
	}

	interface Login3ViewImplCSS extends CssResource {
		String loginError();
		String linksError();
	}
	
	@UiField Login3ViewImplCSS style;
	@UiField(provided=true) String back;
	@UiField(provided=true) String build;
	
	@UiField HTML logoPanel;
	@UiField Widget loginPanel;
	@UiField Frame  messagePanel;
	@UiField Widget linksPanel;
	@UiField Button loginBtn;
	@UiField TextBox username;
	@UiField PasswordTextBox password;
	@UiField Text rb = Text.constants;
	@UiField(provided=true) String pfx;

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	Widget createAndBindUi() {
		back = URL.encodePathSegment(Window.Location.getHref());
		build = "Version " + BUILD.version + "." + BUILD.buildNumber;
		pfx = DWOplayer.PARAMETERS.getResource("");

		return uiBinder.createAndBindUi(this);
	}
	
	public Login3ViewImpl() {
		initWidget(createAndBindUi());
		username.getElement().setPropertyString("placeholder", rb.gebruikersnaam());
		password.getElement().setPropertyString("placeholder", rb.wachtwoord());
		messagePanel.setUrl("//cdn.dwo.nl/resources/alert_"
				+ Text.constants.language()
				+ ".html");
	}
	
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username.getText();
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password.getText();
	}
	boolean allow = true;
	@Override
	public void allowGuest(boolean allow) {
		this.allow = allow;
	}
	@Override
	public void setupModule() {
		// TODO Auto-generated method stub
		
	}
	TapHandler loginHandler, guestHandler;
	@Override
	public HasTapHandlers getLoginBtn() {
		return new HasTapHandlers() {
			
			@Override
			public HandlerRegistration addTapHandler(TapHandler handler) {
				loginHandler = handler;
				return loginBtn.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(! username.getText().isEmpty())
							loginHandler.onTap(null);
					}
				});
			}
		};
	}
	@Override
	public HasTapHandlers getGuestBtn() {
		return new HasTapHandlers() {

			@Override
			public HandlerRegistration addTapHandler(TapHandler handler) {
				guestHandler = handler;
				return loginBtn.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if (username.getText().isEmpty() && allow) 
							guestHandler.onTap(null);
					}
				});
			}
			
		};
	}
	@Override
	public HasAllKeyHandlers getMainPanel() {
		return username;
	}

	public void showError(String string) {
		boolean shown = string != null;
		loginPanel.setStyleName(style.loginError(), shown);
		linksPanel.setStyleName(style.linksError(), shown);
		
	}
	
}
