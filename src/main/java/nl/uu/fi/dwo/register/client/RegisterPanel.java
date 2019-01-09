package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class RegisterPanel extends ResizeComposite {

	private static RegisterPanelUiBinder uiBinder = GWT
			.create(RegisterPanelUiBinder.class);
	private static RegisterBundle bundle = GWT.create(RegisterBundle.class);

	interface RegisterPanelUiBinder extends UiBinder<Widget, RegisterPanel> {
	}

	final private boolean isfree;
	
	public RegisterPanel(boolean isfree, boolean saml) {
	    this.isfree = isfree;
		initWidget(uiBinder.createAndBindUi(this));
		//schoolGroup.addItem(rb.NULLSCHOOL(), RoleType.STUDENT.name());
		schoolGroup.addItem(rb.STUDENT(), RoleType.STUDENT.name());
		schoolGroup.addItem(rb.TEACHER(), RoleType.TEACHER.name());
		schoolGroup.addItem(rb.SCHOOLADMIN(), RoleType.SCHOOLADMIN.name());
		controller = new RegisterController();
		nav_title.setText(isfree ? bundle.REGISTER_FREE() : bundle.REGISTER());
//		register.addTapHandler(this::onRegister);
//		cancel.addTapHandler(this::onCancel);
		setStyleName(css.isfree(), isfree);
		account.setVisible(saml);
		absorbCookies();
		
		
	}

	private void absorbCookies() {
		String email = Cookies.getCookie("email");
		if (email != null) setAndFix(this.email, email);
		String givenName = Cookies.getCookie("givenName");
		if (givenName != null) setAndFix(this.givenName, givenName);
		String insertion = Cookies.getCookie("insertion");
		if (insertion != null) setAndFix(this.insertion, insertion);
		String familyName = Cookies.getCookie("familyName");
		if (familyName != null) setAndFix(this.familyName, familyName);
		
		String suggestion = Cookies.getCookie("suggestion");
		if (suggestion != null) {
			username.setText(suggestion); // Free to choose
		} else {
			String username = Cookies.getCookie("username");
			if (username != null) {
				setAndFix(this.username, username);
			}
		}
		
		String schoolLogin = Cookies.getCookie("schoolLogin");
		if (schoolLogin != null) this.schoolLogin.setText(schoolLogin);
		
		String schoolGroup = Cookies.getCookie("schoolGroup");
		if ("TEACHER".equals(schoolGroup))
			this.schoolGroup.setSelectedIndex(1);
		String schoolCode = Cookies.getCookie("schoolCode");
		if (schoolCode != null) this.schoolCode.setText(schoolCode);
		
	}

	private void setAndFix(TextBox widget, String string) {
		widget.setText(string);
		widget.setEnabled(false);
		widget.addStyleDependentName("disabled");
	}

	private RegisterController controller;
	
	@UiField Button register, cancel;
	@UiField Label nav_title;
	
	@UiField
	DwoLocalesForGWT rb;
	
	@UiField
	TextBox username, givenName, insertion, familyName, email, schoolLogin;
	@UiField
	PasswordTextBox password, passwordAgain, schoolCode;
	
	
	@UiField ListBox schoolGroup;
	@UiField CheckBox account;
	
	@UiField RegisterCSS css;
	
	@UiHandler("account")
	void onValueChange(ValueChangeEvent<Boolean> event) {
		setStyleName(css.saml(), event.getValue());
	}
	
	@UiHandler("cancel")
	void onCancel(ClickEvent e) {
		getController().getCancel().execute();
	}

	@UiHandler("register")
	void registerOrLink(ClickEvent e) {
		if (account.isChecked())
			onLink(e);
		else
			onRegister(e);
	}
	
	
	
	void onLink(ClickEvent e) {
		DomNewUser domUser = new DomNewUser();
		domUser.setUsername(username.getText());
		domUser.setPassword(password.getText());
		if ( ! SimpleValidUserFieldsChecker.isNonEmptyNorNull(domUser.getUsername(), domUser.getPassword())) {
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Required_Fields());
			return;
		}
		domUser.setPassword(MD5.md5(password.getText()));
		controller.link(domUser);
	}

	void onRegister(ClickEvent e) {
		DomNewUser domUser = new DomNewUser();
		
		domUser.setEmail(email.getText());
		domUser.setFamilyName(familyName.getText());
		domUser.setGivenName(givenName.getText());
		domUser.setInsertion(insertion.getText());
		domUser.setUsername(username.getText());
		if(!schoolGroup.getSelectedValue().isEmpty() && !isfree)
		domUser.setRole(RoleType.valueOf(schoolGroup.getSelectedValue()));
		
		String p1 = password.getText();
		String p2 = passwordAgain.getText();
		if (!p1.equals( p2)) {
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_NewPasswordsDoNotMatch());
			return;
		}
		DomNewUser n = domUser; //FIXME java.util.regex niet in GWT
        if ( ! SimpleValidUserFieldsChecker.isNonEmptyNorNull(n.getUsername(), n.getFamilyName(), n.getGivenName(), n.getEmail(), password.getText()))
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
		
		String sLogin = schoolLogin.getText();
		String sCode = schoolCode.getText();
		if(isfree) {
			sLogin = sCode = null;
			domUser.setRole(RoleType.STUDENT);
		} else {
	        if ( ! SimpleValidUserFieldsChecker.isNonEmptyNorNull(sLogin,sCode))
	        {
	            Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Required_Fields());
	            return;
	        }
		}
		domUser.setSchoolCode(sCode);
		domUser.setSchoolLogin(sLogin);		
		
		controller.register(domUser);
		
	}

	public RegisterController getController() {
		return controller;
	}
	

}
