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

	/*
		{ TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Geen gebruiker gevonden met opgegeven gebruikersnaam en wachtwoord" },
		{ TextMapper.EXR_WRONG_USERNAME_PASSWORD, "An user with the specified username and password was not found" },
	 */
	@DefaultStringValue("Geen gebruiker gevonden met opgegeven gebruikersnaam en wachtwoord")
	String EXR_WRONG_USERNAME_PASSWORD();
	
	@DefaultStringValue("Standaard modules")
	String standaardModules();
	@DefaultStringValue("Modules ")
	String schoolModules();
	
	@DefaultStringValue("Geen gegevens ontvangen. Open de activiteit in de editor en sla die opnieuw op")
	String noJSONreceived();

	@DefaultStringValue("Toets verzegeld")
	String lockToetsLabel();
}
