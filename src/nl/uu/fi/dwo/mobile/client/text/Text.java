package nl.uu.fi.dwo.mobile.client.text;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Text extends Constants {
	Text constants = DWOplayer.PARAMETERS.getTextBundle();
	
	@DefaultStringValue("Gebruikersnaam")
	String gebruikersnaam();
	
	@DefaultStringValue("Wachtwoord")
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
	
	@DefaultStringValue("en")
	String language();
	
	@DefaultStringValue("Mijn\u00a0Profiel")
    String GUIMNU_MY_PROFILE();    

	@DefaultStringValue("Check")
	String authELOcheck();
	
	@DefaultStringValue("Help")
	String authELOhelp();
	
	@DefaultStringValue("nl")
	String locale();
	
	@DefaultStringValue("Wachtwoord vergeten?")
	String vergeten();
	
	@DefaultStringValue("Aanmelden")
	String aanmelden();
	
	@DefaultStringValue("Wil je de opdracht afronden?")
	String afronden();
	
	@DefaultStringValue("OK")
	String ok();
	
	@DefaultStringValue("Cancel")
	String cancel();
	
	@DefaultStringValue("Bibliotheek")
	String bibliotheek();
}
