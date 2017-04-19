package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.Button;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class RegisterPanel extends Composite {

	private static RegisterPanelUiBinder uiBinder = GWT
			.create(RegisterPanelUiBinder.class);

	interface RegisterPanelUiBinder extends UiBinder<Widget, RegisterPanel> {
	}

	public RegisterPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		controller = new RegisterController();
	}

	private RegisterController controller;
	
	@UiField
	DwoLocalesForGWT rb = DwoLocalesForGWT.instance;

	@UiField
	Button cancel, register;
	@UiField
	HasText username, password, givenName, insertion, familyName, email, passwordAgain, schoolCode, schoolLogin;

	@UiHandler("cancel")
	void onCancel(TapEvent e) {
		getController().getNext().execute();
	}

	@UiHandler("register")
	void onRegister(TapEvent e) {
		DomNewUser domUser = new DomNewUser();
		
		domUser.setEmail(email.getText());
		domUser.setFamilyName(familyName.getText());
		domUser.setGivenName(givenName.getText());
		domUser.setInsertion(insertion.getText());
		domUser.setUsername(username.getText());
		
		String p1 = password.getText();
		String p2 = passwordAgain.getText();
		if (!p1.equals( p2)) {
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_NewPasswordsDoNotMatch());
			return;
		}
		DomNewUser n = domUser; //FIXME java.util.regex niet in GWT
        if ( ! SimpleValidUserFieldsChecker.isEmptyOrNull(n.getUsername(), n.getFamilyName(), n.getGivenName(), n.getEmail(), password.getText()))
        {
        	Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Required_Fields());
        	return;
        }
		if ( ! SimpleValidUserFieldsChecker.isValidUserName(n.getUsername()))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_UserName_Invalid());
			return;
		}
		if ( ! SimpleValidUserFieldsChecker.isValidEmail(n.getEmail()))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Email_Address_Invalid());
			return;
		}
		if ( ! SimpleValidUserFieldsChecker.isValidPassword(password.getText()))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Password_Invalid());
			return;
		}
		
		domUser.setPassword(MD5.md5(password.getText()));
		
		domUser.setRole(RoleType.STUDENT);
		String sLogin = schoolLogin.getText();
		String sCode = schoolCode.getText();
		if(sLogin.isEmpty()) {
			sLogin = sCode = null;
		}
		domUser.setSchoolCode(sCode);
		domUser.setSchoolLogin(sLogin);		
		
		controller.register(domUser);
		
	}

	public RegisterController getController() {
		return controller;
	}
	

}
