package nl.uu.fi.dwo.mobile.client.text;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.Constants.DefaultStringValue;

public interface Text extends Constants {
	Text constants = GWT.create(Text.class);
	
	@DefaultStringValue("Gebruikersnaam")
	String gebruikersnaam();
	
	@DefaultStringValue("Wachtwoord")
	String wachtwoord();
	
	@DefaultStringValue("Login")
	String login();
	
	@DefaultStringValue("LOG IN ALS GAST")
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
	@DefaultStringValue("Voor leerlingen en studenten")
	String for_students();
	@DefaultStringValue("Het inloggen is mislukt, probeer het opnieuw")
	String inloggen_mislukt();
	@DefaultStringValue("AANMELDEN")
	String AANMELDEN();
	
	@DefaultStringValue("Nog geen account?")
	String register();

	@DefaultStringValue("Meer informatie over Numworx")
	String numworx();
	
	@DefaultStringValue("Logout")
	String logout();
	@DefaultStringValue("Zoek toets of lesstof")
	String search();
	@DefaultStringValue("Aantal resultaten: ")
	String count_results();
	@DefaultStringValue("Geen resultaat: ")
	String no_results();
	@DefaultStringValue("LESSTOF")
	String LESSTOF();
	@DefaultStringValue("ZELFTOETS")
	String ZELFTOETS();
	
	@DefaultStringValue("Wil je niet eerst uitloggen? De gegevens kunnen verloren gaan!")
	String maybe_lost_data();

	@DefaultStringValue("Attempts: ")
	String attemps();

	@DefaultStringValue("TOETS")
	String EINDTOETS();
	
	@DefaultStringValue("U kunt ook inloggen als gast. Uw werk wordt dan NIET opgeslagen.")
	String guestLoginWarning();
	
	@DefaultStringValue("Niet ingelogd")
	String guest();
	
	@DefaultStringValue("<p>Dit is een toets</p><p>Ga naar de <a href='#Exam:' >beveiligde toets omgeving</a> als je deze toets wilt maken</p>")
	String UNSAFE_MODULE_HTML();

	@DefaultStringValue("Dit is een toets")
	String UNSAFE_MODULE_HTML1();
	@DefaultStringValue("Ga naar de ")
	String UNSAFE_MODULE_HTML2();
	@DefaultStringValue("beveiligde toets omgeving")
	String UNSAFE_MODULE_HTML3();
	@DefaultStringValue(" als je deze toets wilt maken")
	String UNSAFE_MODULE_HTML4();

	
	
	@DefaultStringValue("Leerdomeinen")
	String STUDENT_MODELS();
	
	@DefaultStringValue("Afronden")
	String inleveren();
	
	
	@DefaultStringValue("Toets toegangssleutel:")
	String toetssleutel();
	@DefaultStringValue("Ga naar toets")
	String gotoexam();
	
	// Verlopen activiteit
	@DefaultStringValue("De activiteit is verlopen en wordt afgesloten")
	String sco_expired();
	@DefaultStringValue("Over 5 minuten wordt de activiteit afgesloten")
	String sco_almost_expired();
	
// Docent correcties
	@DefaultStringValue("Correctie door de docent")
	String docentCorrectieTitle();
	@DefaultStringValue("Maximale score: ")
	String maximaleScore();
	@DefaultStringValue("Score: ")
	String score();
	@DefaultStringValue("Toevoeging: ")
	String toevoeging();
	@DefaultStringValue("Opmerkingen van de docent:")
	String opmerkingenDocent();
	@DefaultStringValue("Opmerkingen:")
	String opmerkingen();
	
 }
