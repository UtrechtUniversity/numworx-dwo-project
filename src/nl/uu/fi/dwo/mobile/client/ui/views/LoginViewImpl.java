package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.text.Text;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.MPasswordTextBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;

public class LoginViewImpl extends Composite implements LoginView
{

	@UiField FocusPanel focusPanel;
	@UiField Button gastbutton;
	@UiField Button submitbutton;
	@UiField MTextBox username;
	@UiField MPasswordTextBox passwd;
	@UiField Text rb = Text.constants;
	
	private static LoginViewImplUiBinder uiBinder = GWT
			.create(LoginViewImplUiBinder.class);

	interface LoginViewImplUiBinder extends
			UiBinder<Widget, LoginViewImpl> {
	}
	
	public LoginViewImpl()
	{
		
		// loadview using LoginViewImpl.ui.xml
		// style and layout defined in this file
		initWidget(uiBinder.createAndBindUi(this));
		
	}

	
	// TODO - can this function be removed ?
	public void setupModule()
	{
	}

	/**
	 * @return
	 */
	public String getUsername() {
		return username.getText();
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return passwd.getText();
	}

	@Override
	public HasTapHandlers getLoginBtn() {
		return submitbutton;
	}

	@Override
	public HasTapHandlers getGuestBtn() {
		return gastbutton;
	}
	
	@Override
	public HasAllKeyHandlers getMainPanel() {
		return focusPanel;
		
	}
}
