package fi.wiskopdr.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.Constants.DefaultStringValue;

// This is the GWT way:
public interface TextConstants extends ConstantsWithLookup, TextIF {
	@DefaultStringValue("of")
	String ofLabel();
	@DefaultStringValue("en")
	String enLabel();
	
	@DefaultStringValue("Terug")
	String terugKnopLabel();
	
	@DefaultStringValue("CONTROLEER")
	String klaarKnopLabel();
	@DefaultStringValue("kijk na")
	String nakijkKnopLabel();
	@DefaultStringValue("scoregeschiedenis")
	String zelftoetsGeschiedenisKnopLabel();
	@DefaultStringValue("Einde")
	String eindeKnopLabel();
	@DefaultStringValue("Volgende")
	String volgendeKnopLabel();
	@DefaultStringValue("Vorige")
	String vorigeKnopLabel();
	@DefaultStringValue("Totaal: ")
	String totaalScoreLabel();
	@DefaultStringValue(" keer")
	String nakijkLabel();
	@DefaultStringValue("nagekeken")
	String nakijkLabel2();
	@DefaultStringValue("opnieuw")
	String opnieuwKnopLabel();
	@DefaultStringValue("alles opnieuw")
	String allesOpnieuwKnopLabel();
	@DefaultStringValue("Opdracht:")
	String opdrachtLabel();
	@DefaultStringValue("Pagina:")
	String paginaLabel();
	@DefaultStringValue("Onderdeel:")
	String onderdeelLabel();
	@DefaultStringValue("Score:")
	String scoreLabel();
	@DefaultStringValue("Deelscores")
	String objectivesKnopLabel();
	@DefaultStringValue("Analyse")
	String viewMisconceptionsKnopLabel();
	@DefaultStringValue("Categorie")
	String categorieLabel();
	@DefaultStringValue("Score")
	String scoreKopLabel();


	
	@DefaultStringValue("Deze stap bevat correcte en niet correcte onderdelen. Verwijder of vervang de delen die niet correct zijn.")
	String feedbackTekst01();
	@DefaultStringValue("Niet alle oplossingen voldoen aan de oorspronkelijke vergelijking. Verwijder de oplossingen die niet voldoen.")
	String feedbackTekst02();
	@DefaultStringValue("De ongelijkheid is correct opgelost.")
	String feedbackTekst03();
	@DefaultStringValue("De vergelijking is correct opgelost.")
	String feedbackTekst04();
	@DefaultStringValue("Geef de gevraagde afronding.")
	String feedbackTekst05();
	@DefaultStringValue("Geef nu de oplossing(en) van de ongelijkheid.")
	String feedbackTekst06();
	@DefaultStringValue("Er ontbreken oplossingen. Vul aan.")
	String feedbackTekst07();
	@DefaultStringValue("Gebruik geen absoluut strepen ( bv: |x-3| ).")
	String feedbackTekst08();
	@DefaultStringValue("De notatie is niet juist.")
	String feedbackTekst09();
	@DefaultStringValue("Oplossing is goed, maar nog niet in de juiste vorm.")
	String feedbackTekst10();
	@DefaultStringValue("Deze benadering is een goede oplossing.")
	String feedbackTekst11();
	@DefaultStringValue("De notatie van het antwoord klopt niet.")
	String feedbackTekst14();
	@DefaultStringValue("Dit is een correcte vergelijking")
	String feedbackTekst16();
	@DefaultStringValue("Deze vergelijking heeft niet de gevraagde vorm")
	String feedbackTekst17();
	@DefaultStringValue("De oplossing is goed, maar het aantal significante cijfers klopt niet.")
	String feedbackTekst18();
	@DefaultStringValue("Oplossing is goed, maar nog niet in de juiste vorm en de significantie klopt niet.")
	String feedbackTekst19();
	@DefaultStringValue("Oplossing is goed, significantie klopt maar heeft nog niet in de juiste vorm.")
	String feedbackTekst20();
	@DefaultStringValue("Je hebt alle oplossingen gevonden, vul ze onderaan in.")
	String feedbackTekst21a();
	@DefaultStringValue("Je hebt alle oplossingen gevonden.")
	String feedbackTekst21b();
	@DefaultStringValue("Je hebt de oplossingen in deze tak gevonden, ga verder met een andere tak.")
	String feedbackTekst22();
	
	
	
	//pijlvak
	@DefaultStringValue("  haakjes") 
	String haakjesLabel0();
	@DefaultStringValue("    weg") 
	String haakjesLabel1();
	@DefaultStringValue("   herleid") 
	String herleidLabel0();
	@DefaultStringValue("") 
	String herleidLabel1();
	@DefaultStringValue("  ontbind") 
	String ontbindLabel0();
	@DefaultStringValue("  splits") 
	String splitsLabel0();
	@DefaultStringValue("  wortels") 
	String wortelLabel0();
	@DefaultStringValue("  gelijkwaardig") 
	String gelijkwaardigLabel0();
	@DefaultStringValue("    met:") 
	String gelijkwaardigLabel1();
	@DefaultStringValue("Substitueer:") 
	String subLabel();
	
	@DefaultStringValue("geen oplossingen") // vergelijking.java
	String geenOplossingen();
	@DefaultStringValue("alles is een oplossing") // vergelijking.java
	String allesOplossing();
	@DefaultStringValue("geen")
	String antwoordModelGeen();
	@DefaultStringValue("abc")
	String abc();
	
	@DefaultStringValue("Kies")
	String keuzeVakKiesLabel();
	
	@DefaultStringValue("Actie")
	String actionLabel();
// Opnieuw dialog
/*
 * 	{ "opnieuwPanelTekstMW", "Je verliest je huidige score als je opnieuw begint.\n\nWeet je zeker dat je opnieuw wilt beginnen?" },
	{ "opnieuwPanelTekst1", "Je verliest je huidige scores als je opnieuw begint." },
	{ "opnieuwPanelTekst2", "Weet je zeker dat je opnieuw wilt beginnen?" },
	{ "opnieuwPanelTitel", "Opnieuw?" },
	{ "jaTekst", "Ja"},
	{ "neeTekst", "Nee"},
	
 */
	@DefaultStringValue("Je verliest je huidige scores als je opnieuw begint.")
	String opnieuwPanelTekst1();
	@DefaultStringValue("Weet je zeker dat je opnieuw wilt beginnen?")
	String opnieuwPanelTekst2();
	@DefaultStringValue("Opnieuw?")
	String opnieuwPanelTitel();
	@DefaultStringValue("Ja")
	String jaTekst();
	@DefaultStringValue("Nee")
	String neeTekst();
	
	@DefaultStringValue("Uitvoeren")
	String executeLabel();
	
	@DefaultStringValue("sub")
	String sub();
	
	@DefaultStringValue("Oplossingen")
	String oplossingenLabel();
	
	@DefaultStringValue("Je hebt momenteel geen internetverbindig. Je werk kan niet worden opgeslagen")
	String noInternet();
	@DefaultStringValue("De server geeft een fout terug. Je werk kan niet worden opgeslagen")
	String serverError();
	
	//FEWS
	@DefaultStringValue("Nieuwe regel")
	String tooltip_downButton();
	@DefaultStringValue("Verwijder regel")
	String tooltip_terugButton();
	@DefaultStringValue("Kopieer regel")
	String tooltip_copyButton();
	@DefaultStringValue("Aan beide kanten optellen")
	String tooltip_plusKnop();
	@DefaultStringValue("Aan beide kanten aftrekken")
	String tooltip_minKnop();
	@DefaultStringValue("Vermenivuldig beide kanten")
	String tooltip_maalKnop();
	@DefaultStringValue("Deel beide kanten")
	String tooltip_deelKnop();
	@DefaultStringValue("Haakjes wegwerken")
	String tooltip_haakjesKnop();
	@DefaultStringValue("Termen samennemen")
	String tooltip_herleidKnop();
	@DefaultStringValue("Ontbinden")
	String tooltip_ontbindKnop();
	@DefaultStringValue("Splits bij ontbinding op 0")
	String tooltip_splitsKnop();
	@DefaultStringValue("Trek aan beide kanten de wortel")
	String tooltip_wortelBewerkKnop();
	@DefaultStringValue("Gebruik een substitutie")
	String tooltip_subKnop();
	@DefaultStringValue("Bereken de discriminant")
	String tooltip_abcKnop();
	@DefaultStringValue("Rekenmachine")
	String tooltip_rmKnop();
	
	//FE
	@DefaultStringValue("Formule invoegen")
	String tooltip_formuleButton();
	@DefaultStringValue("Berekening invoegen")
	String tooltip_calcButton();
	
	
	//Popup
	@DefaultStringValue("Uitwerking")
	String popupUitwerkingTitel();
	
	String classesViewDescription();
	
// enzovoort
}