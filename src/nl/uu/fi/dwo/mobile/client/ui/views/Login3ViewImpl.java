/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.BUILD;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.text.Text;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import dagger.Reusable;

/**
 * @author wim
 *
 */
@Reusable
public class Login3ViewImpl extends Composite implements LoginView  {

	private static Login3ViewImplUiBinder uiBinder = GWT
			.create(Login3ViewImplUiBinder.class);

	interface Login3ViewImplUiBinder extends UiBinder<Widget, Login3ViewImpl> {
	}

	interface Login3ViewImplCSS extends CssResource {
		String loginError();
		String linksError();
		String noGuestAllowed();
	}
	
	@UiField Login3ViewImplCSS style;
	@UiField(provided=true) String back;
	@UiField(provided=true) String build;
	@UiField(provided=true) String for_students;
	
	@UiField HTML logoPanel;
	@UiField Widget loginPanel;
	@UiField Frame  messagePanel;
	@UiField Widget linksPanel;
	@UiField Button loginBtn;
	@UiField TextBox username;
	@UiField PasswordTextBox password;
	@UiField(provided=true) Text rb = Text.constants;
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
	Widget createAndBindUi(DWOplayerParameters PARAMETERS) {
		back = URL.encodePathSegment(Window.Location.getHref());
		build = "Version " + BUILD.version + "." + BUILD.buildNumber;
        pfx = PARAMETERS.getResource("");
		for_students = rb.for_students();
		if(kiosk)
			for_students = //rb.kiosk_mode
				rb.kiosk_mode();

		return uiBinder.createAndBindUi(this);
	}
	
	private final boolean kiosk;
	
	@Inject Login3ViewImpl(DWOplayerParameters PARAMETERS) {
	    kiosk = PARAMETERS.getSecureMode() != SecureMode.NORMAL;
	    allow = !kiosk;
		initWidget(createAndBindUi(PARAMETERS));
		username.getElement().setPropertyString("placeholder", rb.gebruikersnaam());
		username.getElement().setAttribute("autocomplete", "off");
		username.getElement().setAttribute("autocapitalize", "off");
		password.getElement().setPropertyString("placeholder", rb.wachtwoord());
		password.getElement().setAttribute("autocapitalize", "off");
		password.getElement().setAttribute("autocomplete", "off");
		
		if(!kiosk)
		{		
			messagePanel.setUrl("//cdn.dwo.nl/resources/alert_"
				+ Text.constants.language()
				+ ".html");
		} else {
			messagePanel.removeFromParent();
			linksPanel.removeFromParent();
			allowGuest(false);
		}
		
	}
	
	@Override
	public String getUsername() {
		return username.getText();
	}
	@Override
	public String getPassword() {
		return password.getText();
	}
	
	boolean allow;

	@Override
	public void allowGuest(boolean allow) {
		this.allow = allow && !kiosk;
		loginPanel.setStyleName(style.noGuestAllowed(), !this.allow);
	}
	@Override
	public void setupModule() {
		// TODO Auto-generated method stub
		
	}
	ClickHandler loginHandler, guestHandler;
	@Override
	public HasClickHandlers getLoginBtn() {
		return new HasClickHandlers() {
			
			@Override
			public HandlerRegistration addClickHandler(ClickHandler handler) {
				loginHandler = handler;
				return loginBtn.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(! username.getText().isEmpty())
							loginHandler.onClick(event);
					}
				});
			}

			@Override
			public void fireEvent(GwtEvent<?> event) {
				loginBtn.fireEvent(event);
			}
		};
	}
	@Override
	public HasClickHandlers getGuestBtn() {
		return new HasClickHandlers() {

			@Override
			public HandlerRegistration addClickHandler(ClickHandler handler) {
				guestHandler = handler;
				return loginBtn.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if (username.getText().isEmpty() && allow) 
							guestHandler.onClick(event);
					}
				});
			}

			@Override
			public void fireEvent(GwtEvent<?> event) {
				loginBtn.fireEvent(event);
			}
			
		};
	}
	
	HasKeyUpHandlers h = new HasKeyUpHandlers() {
		
		KeyUpHandler up;
		@Override
		public void fireEvent(GwtEvent<?> event) {
			if(up != null && !username.getText().isEmpty())
				up.onKeyUp((KeyUpEvent) event);
			
		}
		
		@Override
		public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
			up = handler;
			return new HandlerRegistration() {
				
				@Override
				public void removeHandler() {
					if(up == handler)
						up = null;
				}
			};
		}
	};
	
	@Override
	public HasKeyUpHandlers getMainPanel() {
		return h;
	}

	public void showError(String string) {
		boolean shown = string != null;
		loginPanel.setStyleName(style.loginError(), shown);
		linksPanel.setStyleName(style.linksError(), shown);
		
	}
	
	@UiHandler({"username","password" })
	void onKeyUpUser(KeyUpEvent up) {
		h.fireEvent(up);
	}
	
	@UiHandler({"guestBtn"}) 
	void onGuestBtn(ClickEvent e) {
		if (allow && guestHandler != null) 
			guestHandler.onClick(e);	
	}
}
