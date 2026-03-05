package nl.uu.fi.dwo.register.client;

import java.util.logging.Level;
import java.util.logging.Logger;

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

	static Logger LOG = Logger.getLogger("RegisterPanel");
	
	private static RegisterPanelUiBinder uiBinder = GWT
			.create(RegisterPanelUiBinder.class);
	private static RegisterBundle bundle = GWT.create(RegisterBundle.class);

	interface RegisterPanelUiBinder extends UiBinder<Widget, RegisterPanel> {
	}

	final private boolean isfree;
	final private boolean demo;
	private static final String WISWISE_FREE = "WISWISE-FREE";
	final private boolean wiswise;
	final private boolean saml;
	
	public RegisterPanel(boolean isfree, boolean saml) {
	    this.isfree = isfree;
	    this.saml = saml;
	    demo = "DEMO".equalsIgnoreCase(getCookie("form"));
	    wiswise = WISWISE_FREE.equalsIgnoreCase(getCookie("form"));
	    
		initWidget(uiBinder.createAndBindUi(this));
		fixInputs();
		//schoolGroup.addItem(rb.NULLSCHOOL(), RoleType.STUDENT.name());
		schoolGroup.addItem(rb.STUDENT(), RoleType.STUDENT.name());
		schoolGroup.addItem(rb.TEACHER(), RoleType.TEACHER.name());
		schoolGroup.addItem(rb.SCHOOLADMIN(), RoleType.SCHOOLADMIN.name());
		controller = new RegisterController(this);
		nav_title.setText(isfree  && !demo ? bundle.REGISTER_FREE() : bundle.REGISTER());
//		register.addTapHandler(this::onRegister);
//		cancel.addTapHandler(this::onCancel);
		setStyleName(css.isfree(), isfree);
		setStyleName(css.issaml(), saml);
		account.setVisible(saml);
		absorbCookies();
		
		
	}

	private void fixInputs() {
		password.getElement().setAttribute("autocomplete", "new-password");
		passwordAgain.getElement().setAttribute("autocomplete", "new-password");
		schoolCode.getElement().setAttribute("autocomplete", "new-password");		
	}

	static String getCookieOnce(String key) {
	  String r = getCookie(key);
	  Cookies.removeCookie(key, "/"); // ons kent ons.
	  return r;
	}
		
	private void absorbCookies() {
		String email = getCookieOnce("email");
		if (email != null) setAndFix(this.email, email);
		String givenName = getCookieOnce("givenName");
		if (givenName != null) setAndFix(this.givenName, givenName);
		String insertion = getCookieOnce("insertion");
		String familyName = getCookieOnce("familyName");
		boolean empty = familyName == null || familyName.isEmpty();
		if (insertion != null) setAndFix(this.insertion, insertion, empty);
		
		if (familyName != null) setAndFix(this.familyName, familyName);
		
		String suggestion = getCookieOnce("suggestion");
		if (suggestion != null) {
			username.setText(suggestion); // Free to choose
			realm = realmOf(suggestion);
		} else {
			String username = getCookieOnce("username");
			if (username != null) {
				setAndFix(this.username, username);
				realm = realmOf(username);
			} else 
				realm = "";
		}
		
		
		String schoolLogin = getCookieOnce("schoolLogin");
		if (schoolLogin != null) this.schoolLogin.setText(schoolLogin);
		
		String schoolGroup = getCookieOnce("schoolGroup");
		if ("TEACHER".equals(schoolGroup))
		{
			this.schoolGroup.setSelectedIndex(1);
			LOG.info("school group = " + this.schoolGroup.getSelectedItemText() + " " + this.schoolGroup.getSelectedValue());
		}
		String schoolCode = getCookie("schoolCode");
		if (schoolCode != null) {
		  this.schoolCode.setText(schoolCode);
		  Cookies.removeCookie("schoolCode", "/");
		}
		String schoolClass = getCookieOnce("className");
		if (schoolClass != null && !schoolClass.isEmpty() && "STUDENT".equals(schoolGroup)) {
			this.schoolClass = schoolClass;
			setAndFix(this.schoolLogin, schoolLogin);
			setAndFix(this.schoolCode, schoolCode);
			this.schoolGroup.setEnabled(false);
			this.schoolGroup.addStyleDependentName("disabled");
		}
		
		String putRequest = getCookie("putRequest");
		if (putRequest != null) {
		  Cookies.removeCookie("putRequest", "/");
		  controller.setPutRequest(putRequest);
		}
		
	}

	private String realmOf(String suggestion) {
		int l = suggestion.lastIndexOf('@');
		if (l < 0) return "";
		return suggestion.substring(l);
	}

	static String getCookie(String string) {
		String result = Cookies.getCookie(string);
		LOG.info("VOOR " + string + "="  + result);
		if (result == null) return null;
		if(result.startsWith("\"")) {
			result = result.substring(1, result.length()-1); // cookie decoder!
			for(int i = 0; i < result.length(); i++) {
				if (result.charAt(i)=='\\')
					result = result.substring(0, i) + result.substring(i+1);
			}
		}
		try {
			result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			LOG.log(Level.WARNING, "decoding "  + result, e);
		}
		LOG.info("NA + " + string + "=" + result +" !");
		return result.trim();
	}

	private void setAndFix(TextBox widget, String string) {
		setAndFix(widget, string, true);
	}
	private void setAndFix(TextBox widget, String string, boolean force) {
		widget.setText(string);
		if (force && (string == null || string.isEmpty())) 
			return;
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

	private String schoolClass;

	private String realm;
	
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
		if(!schoolGroup.getSelectedValue().isEmpty() && !isfree || demo)
			domUser.setRole(RoleType.valueOf(schoolGroup.getSelectedValue()));
		
		
		String p1 = password.getText();
		String p2 = passwordAgain.getText();
		if (!p1.equals( p2)) {
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_NewPasswordsDoNotMatch());
			return;
		}
		DomNewUser n = domUser; //FIXME java.util.regex niet in GWT
        if ( ! SimpleValidUserFieldsChecker.isNonEmptyNorNull(n.getUsername(), n.getFamilyName(), n.getGivenName())
// password alleen verplicht bij !saml
        		|| !saml && ! SimpleValidUserFieldsChecker.isNonEmptyNorNull(n.getEmail(), password.getText() )
        		)
        {
        	Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Required_Fields());
        	return;
        }
		String sLogin = schoolLogin.getText();
		String check = n.getUsername();
// realm of <sLogin> is @<sLogin>		
		if (check.endsWith("@" + sLogin)) {
			check = check.substring(0, check.length()- sLogin.length()-1);
		} else if (check.endsWith(realm)) {
			check = check.substring(0, check.length()-realm.length());
		}
		if ( ! SimpleValidUserFieldsChecker.isValidUserName(check))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_UserName_Invalid());
			return;
		}
// alleen bij saml: valid or empty
		if ( ( !saml || !n.getEmail().isEmpty()) &&
				! SimpleValidUserFieldsChecker.isValidEmail(n.getEmail()))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Email_Address_Invalid());
			return;
		}
		if ( ( !saml || !password.getText().isEmpty()) &&			
				! SimpleValidUserFieldsChecker.isValidPassword(password.getText()))
		{
			Window.alert(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_Registration_Password_Invalid());
			return;
		}
		if (password.getText().isEmpty())
			domUser.setPassword("");
		else domUser.setPassword(MD5.md5(password.getText()));
		
		String sCode = schoolCode.getText();
		if(isfree && !demo) {
			if (!wiswise) sLogin = sCode = null;
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

	public String getSchoolClass() {
		return schoolClass;
	}
	

}
