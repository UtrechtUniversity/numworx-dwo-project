package nl.uu.fi.dwo.mobile.client.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Text extends Constants {
	Text constants = GWT.create(Text.class);
	
	@DefaultStringValue("Gebruikersnaam:")
	String gebruikersnaam();
	
	@DefaultStringValue("Wachtwoord:")
	String wachtwoord();
	
	@DefaultStringValue("Login")
	String login();
	
	@DefaultStringValue("Login als gast")
	String loginAlsGast();
	
	@DefaultStringValue("Digitale Wiskunde Omgeving")
	String dwo();
	
	@DefaultStringValue("Freudenthal Instituut")
	String fi();

}
