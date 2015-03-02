//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.commons.system.text;

import java.util.ListResourceBundle;

import fi.dwo.commons.system.TextMapper;

public class Text_de extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Gast"},
         { TextMapper.BTN_LOGIN, "Anmelden" },
         { TextMapper.BTN_CANCEL, "Abbrechen" },
         { TextMapper.BTN_NO, "Nein" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Ja" },
         { TextMapper.BTN_CLOSE, "Schließen"},
         { TextMapper.DLG_CONFIRM, "Bestätigen" },
         { TextMapper.DLG_ENTER_INPUT, "Eingabe" },
         { TextMapper.DLG_MESSAGE, "Hinweis" },

         { TextMapper.EX_UNKNOWN_ERROR, "Ein interner Fehler ist aufgetreten" },
         { TextMapper.EXR_USER_EXISTS, "Dieser Benutzername existiert bereits" },
         { TextMapper.EXR_USER_EXISTS2, "Der Benutzername {0} wird bereits verwendet" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "Die beiden Kennwörter sind verschieden" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Es gibt keinen Benutzer mit diesem Namen und Kennwort" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Unbekannte Kombination von Schule/Gruppe/Kennwort" },
         { TextMapper.EXR_MANDATORY, "Das Feld {0} in {1} wurde nicht ausgefüllt, ist aber notwendig"},
         { TextMapper.EXR_WRONG_FORMAT, "Das Feld {0} in {1} enthält unzulässige Zeichen" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "Das Feld {0} in {1} ist unzulässig" },
         { TextMapper.EXC_CLASS_EXISTS, "Die angegebene Klasse existiert bereits" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Eine Schule mit dieser Kennung existiert bereits" },
         { TextMapper.EXL_UNKNOWN_USER, "Ein Benutzer mit dieser Kennung ist nicht bekannt" },
         { TextMapper.EXC_COURSE_EXISTS, "Dieses c-Book existiert bereits" },
         { TextMapper.EXS_SCO_EXISTS, "Diese Lerneinheit existiert bereits" },
         { TextMapper.EXS_NO_APPLET, "Applet wurde nicht gefunden"},

         { TextMapper.GUI_WAIT_A_MOMENT, "Einen Moment bitte"},

         { TextMapper.GUIW_LOGINDATA, "Anmeldedaten" },
         { TextMapper.GUIW_USERNAME, "Benutzername" },
         { TextMapper.GUIW_PASSWORD, "Kennwort" },
         { TextMapper.GUIW_WELCOME, "Willkommen" },
         { TextMapper.GUIW_GUESTLOGIN, "Als Gast anmelden" },
         { TextMapper.GUIW_REGISTER, "Registrieren" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Ihre Änderungen werden nicht gespeichert" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "Als neues Mitglied registrieren." },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Als Gast anmelden" },
         { TextMapper.GUIW_BTN_LOGIN, "Anmelden" },
         { TextMapper.GUIW_BTN_REGISTER, "Registrieren" },
         { TextMapper.GUIW_ERR_LOGIN, "Anmeldefehler" },

         { TextMapper.GUIR_REGISTER, "Registrieren" },

         { TextMapper.GUIR_REGISTERINFO, "Neue Benutzerregistrierung" },
         { TextMapper.GUIR_PERSONALINFO, "Persönliche Daten" },
         { TextMapper.GUIR_SCHOOLINFO, "Schuldaten" },

         { TextMapper.GUIR_USERNAME, "Benutzername" },
		 { TextMapper.GUIR_PASSWORD, "Kennwort" },
		 { TextMapper.GUIR_RE_PASSWORD, "Kennwortwiederholung" },

		 { TextMapper.GUIR_FIRSTNAME, "Vorname" },
		 { TextMapper.GUIR_MIDDLENAME, "Zweiter Vorname" },
		 { TextMapper.GUIR_LASTNAME, "Nachname" },
		 { TextMapper.GUIR_EMAIL, "E-Mail" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "Schulanmeldung" },
		 { TextMapper.GUIR_SCHOOLGROUP, "I am" }, // no idea what that is
		 { TextMapper.GUIR_SCHOOLPASSWORD, "Kennwort" },

		 { TextMapper.GUIR_BTN_REGISTER, "Registrieren" },
		 { TextMapper.GUIR_BTN_RESET, "Zurücksetzen" },
         { TextMapper.GUIR_BTN_BACK, "Zurück zu den c-Books" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Durch die Schule übermittelte Daten" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "Auswahl" },
            { TextMapper.GUIR_OPT_STUDENT, "SchülerIn" },
            { TextMapper.GUIR_OPT_TEACHER, "LehrerIn" },
            { TextMapper.GUIR_OPT_ADMIN, "Verwaltung"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "Schul-Administrator" },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Schulcode"},
            
            { TextMapper.GUIR_ERR_REGISTER, "Es ist ein Fehler aufgetreten" },

            { TextMapper.GUIR_MSG_REGISTERED, "Sie wurden erfolgreich registriert"},

            { TextMapper.GUIM_DWO_FULL, "Digitale Mathematik Umgebung" },
            { TextMapper.GUIM_DWO_SHORT, "DMU" },
            { TextMapper.GUIM_FI_NAME, "Freudenthal Institut"},
            { TextMapper.GUIM_MAIN_MENU, "c-Books" },

            { TextMapper.GUIL_LOGGED_IN_AS, "Angemeldet als" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "Nicht angemeldet"},
            { TextMapper.GUIL_BTN_LOGIN, "Anmelden"},
            { TextMapper.GUIL_BTN_LOGOFF, "Abmelden" },

            { TextMapper.GUIMNU_MAIN_MENU, "c-Books" },
            { TextMapper.GUIMNU_MY_PROFILE, "Mein Profil" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "SchülerIn der Klasse" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Du bist noch "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "in keiner Klasse.  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Gehe zu"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Mein Profil\" und "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "wähle eine Klasse."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Ergebnisse der Klasse" },
            { TextMapper.GUIMNU_RESULTS, "Ergebnisse ansehen" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Klasse verwalten" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Schule verwalten" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "c-Books verwalten"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Name der neuen Klasse" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Neue Klasse hinzufügen" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Name der neuen Schule"},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Neue Schule hinzufügen"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "Benutzer dieser Schule"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "Klassen dieser Schule"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Optionen für Administratoren"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Benutzer verwalten"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Nur aus dieser Schule entfernen"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Benutzer komplett löschen"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Neue SchülerInnen hinzufügen"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Neue LehrerInnen hinzufügen"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Aus der Zwischenlage importieren"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Kennungen erzeugen"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Zusätzliche Zeile"},

         	{ TextMapper.GUICO_HEADER, "c-Books"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "Lerneinheiten"},

            { TextMapper.GUIP_MY_PROFILE, "Mein Profil" },
            { TextMapper.GUIP_REGISTERINFO, "Registrierungsdaten" },
            { TextMapper.GUIP_PERSONALINFO, "Persönliche Daten" },
            { TextMapper.GUIP_SCHOOLINFO, "Daten der Schule" },

            { TextMapper.GUIP_USERNAME, "Benutzername" },
            { TextMapper.GUIP_OLD_PASSWORD, "Aktuelles Kennwort" },
            { TextMapper.GUIP_PASSWORD, "Neues Kennwort" },
            { TextMapper.GUIP_RE_PASSWORD, "Neues Kennwort wiederholen" },

            { TextMapper.GUIP_FIRSTNAME, "Vorname" },
            { TextMapper.GUIP_MIDDLENAME, "Zweiter Vorname" },
            { TextMapper.GUIP_LASTNAME, "Nachname" },
            { TextMapper.GUIP_EMAIL, "E-Mail" },

            { TextMapper.GUIP_SCHOOLLOGIN, "Schulanmeldung" },
            { TextMapper.GUIP_SCHOOLGROUP, "I am" }, // what is this?
            { TextMapper.GUIP_SCHOOLPASSWORD, "Kennwort" },
            { TextMapper.GUIP_CLASS, "Klasse" },

            { TextMapper.GUIP_BTN_SAVE, "Speichern" },
            { TextMapper.GUIP_BTN_RESET, "Zurücksetzen" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Profil löschen" },

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Durch die Schule übermittelte Daten" },

            { TextMapper.GUIP_ERR_CHANGE, "Es ist ein Fehler aufgetreten" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "Auswahl" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Sind Sie sicher, dass Sie ihre Benutzerkennung löschen möchten?" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Benutzerkennung löschen" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Ihr Konto wurde erfolgreich geändert"},

            { TextMapper.GUIPT_SCHOOL, "Schule" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "LehrerIn der Klasse" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Klasse hinzufügen" },
            
         { TextMapper.GUIS_STUDENTS, "SchülerInnen"},
         { TextMapper.GUIS_TEACHERS, "LehrerInnen"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Schulverwaltung"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Die Schule {0} löschen "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Schule umbenennen"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "SchülerInnen von {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Schule hinzufügen"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Schule löschen"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Schule umbenennen"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Geben Sie einen neuen Schulnamen ein"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Diese Schule wirklich löschen?"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "Dieser Schule sind Benutzerkennungen zugeordnet. Wirklich löschen?"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "Dieser Schule sind Benutzerkennungen zugeordnet."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Möchten Sie {0} wirklich aus dieser Schule entfernen?"},
         { TextMapper.GUIS_DELETE_STUDENT, "SchülerIn aus dieser Schule entfernen"},
         { TextMapper.GUIS_NO_STUDENTS, "Der Schule {0} sind keine SchülerInnen zugeordnet"}, 

         { TextMapper.GUIC_STUDENTS, "SchülerInnen"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Klassenverwaltung"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Die Klasse {0} löschen"},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Klassenname bearbeiten"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "SchülerInnen der Klasse {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "c-Book der Klasse {0} zuweisen"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "SchülerInnen" },
            { TextMapper.GUIC_ADD_CLASS, "Klasse erstellen"},
            { TextMapper.GUIC_DELETE_CLASS, "Klasse löschen" },
            { TextMapper.GUIC_RENAME_CLASS, "Klasse umbenennen" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "Neuer Name der Klasse" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "Sind Sie sicher, dass Sie diese Klasse löschen möchten?" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "Dieser Klasse sind SchülerInnen zugeordnet. Dennoch löschen?" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Dieser Klasse sind SchülerInnen zugeordnet" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Möchten Sie {0} wirklich aus der Klasse entfernen?"},
         { TextMapper.GUIC_DELETE_STUDENT, "SchülerIn aus Klasse entfernen"},
         { TextMapper.GUIC_NO_STUDENTS, "Der Klasse {0} sind keine SchülerInnen zugeordnet"},

         { TextMapper.GUIRS_RESULTS, "Ergebnisse"},
         { TextMapper.GUIRS_NO_RESULTS, "Keine Ergebnisse"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "c-Books auswählen"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "c-Books auswählen"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Ergebnisse für {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Nach {0} sortieren"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Ergebnisse der Lerneinheit {0} von {1} anzeigen"},
         { TextMapper.GUIRSDLG_MSG, "Alle Ergebnisse von ''{0}'' bei {1} löschen?"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "Ergebnisse der Lerneinheit {0} von {1}"},

         { TextMapper.GUISC_TITLE, "c-Books auswählen"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Alle auswählen"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Keine auswählen"},

         { TextMapper.UG_CLASSES, "Klassen"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "SchülerInnen von {0}"},

         { TextMapper.UG_USER_TITLE,"SchülerIn"},
         { TextMapper.UG_CLASS_TITLE,"Klasse"},

         { TextMapper.UG_CLASS_CHILD, "SchülerInnen {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Alphabetisch nach Klasse"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Alphabetisch nach Klasse (absteigend)"},

         { TextMapper.UG_USER_PARENT, "Klassen"},
         { TextMapper.UG_USER_ORDER_ASC, "Alphabetisch nach Nachname"},
         { TextMapper.UG_USER_ORDER_DESC, "Alphabetisch nach Nachname (absteigend)"},

         { TextMapper.LG_COURSES, "c-Books"},
         { TextMapper.LG_SCOS_OF_COURSE, "Lerneinheiten in {0}"},

         { TextMapper.LG_COURSE_CHILD, "Lerneinheiten in {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "Ergebnis (aufsteigend)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "Ergebnis (absteigend)"},

         { TextMapper.LG_SCO_PARENT, "c-Books"},
         { TextMapper.LG_SCO_ORDER_ASC, "Ergebnis (aufsteigend)"},
         { TextMapper.LG_SCO_ORDER_DESC, "Ergebnis (absteigend)"},

         { TextMapper.LG_SCO_NAME , "Lerneinheit {0}"},

         { TextMapper.GUIC_ADD_COURSE, "Neues c-Book hinzufügen"},
         { TextMapper.GUIC_ADD_MAP, "Neuen Ordner hinzufügen" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "c-Books verwalten"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "c-Book {0} löschen"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Ordner {0} löschen"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "c-Book bearbeiten"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Lerneinheiten verwalten"},

         { TextMapper.GUICDLG_COURSE_NAME, "c-Book-Name"},
         { TextMapper.GUICDLG_MAP_NAME, "Ordnername" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Beschreibung"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Neues Modul hinzufügen"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Modul bearbeiten"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Ordner bearbeiten" },

         { TextMapper.GUIC_NO_COURSES, "Keine Module"},
         { TextMapper.GUIC_COURSE_SHARE, "Modules teilen" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "In diesem Modul sind Lerneinheiten. \nWenn Sie das Modul löschen \nwerden die Ergebnisse der Lerneinheiten ebenfalls gelöscht.\n \nWollen Sie das Modul trotzdem löschen?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Möchten Sie das Modul wirklich löschen?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Modul löschen"},

         { TextMapper.GUIS_ADD_SCO, "Neue Lerneinheit hinzufügen"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Lerneinheiten im Modul {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Lerneinheitenverwaltung"},
         { TextMapper.GUIS_SHOW_SCORE, "SchülerInnen können ihre Ergebnisse sehen"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "Lerneinheit {0} löschen"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Lerneinheit umbenennen"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Lerneinheit bearbeiten"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Zurück zu den Modulen"},

         { TextMapper.GUISDLG_SCO_NAME, "Name der Lerneinheit"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Beschreibung der Lerneinheit"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Neue Lerneinheit hinzufügen"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Lerneinheit bearbeiten"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "Wenn Sie die Lerneinheit löschen \nwerden die Ergebnisse ebenfalls gelöscht.\n \nMöchten Sie die Lernheit wirklich löschen?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Lerneinheit löschen"},
         { TextMapper.GUIS_NO_SCOS, "Keine Lerneinheiten im Modul {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "Logo von {0} laden"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Hinzufügen"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Vorschau der Lerneinheit"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Lerneinheit wählen"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "Es gibt keine hinzufügbare Lerneinheit"},
         { TextMapper.GUISDLG_SHOW, "Zeigen"},
         { TextMapper.GUISDLG_ALL, "Alle"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "Sie haben keine Lerneinheit ausgewählt"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Standard-Lerneinheiten"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Eigene Lerneinheiten"},

         { TextMapper.GUIPA_BTN_PREVIEW, "Vorschau der Lerneinheit"},
         { TextMapper.GUIPA_BTN_SAVE, "Speichern"},
         { TextMapper.GUIPA_BTN_RESET, "Zurücksetzen"},
         { TextMapper.GUIPA_BTN_CANCEL, "Schließen"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Lerneinheit bearbeiten"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Diese Lerneinheit kann nicht verändert werden"},

         { TextMapper.GUIPA_DLG_TTL, "Bearbeitungsmodus der Lerneinheit {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Wenn Sie diese Einstellungen speichern,\n werden ältere Ergebnisse gelöscht.\n \nMöchten Sie die Einstellungen wirklich sichern?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Einstellungen sichern"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Parameter ({0})"},
         
         { "cut", "Ausschneiden" },
         { "copy" , "Kopieren" },
         { "paste", "Einfügen"},
         { "delete", "Löschen" },
         { "edit", "Bearbeiten" },
         { "file", "Datei" },
         { "rename", "Umbenennen" },

         { TextMapper.GUIA_INSERT_SCOS, "Lerneinheiten aus Sicherheitskopie einfügen"},
         { TextMapper.GUIH_STOP_EDIT, "Bearbeiten beenden" },
         { TextMapper.GUIH_EDIT, "Bearbeiten" },
         
         { "Alle modules", "Alle Module"},
         { "Standaard DWO modules", "Standard DMU-Module"},

         { "Nieuwe Modulemap", "Neuer Modulordner" },

         // classadminpanel
         { "Klassen toewijzen", "Klassen zuweisen" },
         { "Klas", "Klassen" },
         { "Docent", "LehrerIn" },
         { "Verwijder", "Entfernen" },
         // classpanel 
         { "boomstructuur?", "Baumansicht?" },
         // select courses dialog
         { "Leerlinggegevens verwijderen", "Ergebnisse der SchülerInnen löschen" },
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "Möchten Sie alle Ergebnisse von {0} in {1} entfernen?" },
         { "soort", "Art" },
         { "vanaf", "von" },
         { "tot aan", "bis" },
         { "tot", "bis" },
         { "Ll ggvns", "Ergebnisse" },
         { "normaal", "normal" },
         { "afgeschermd", "verborgen" },
         { "Geef tijdstip {0}", "Zeit und Dateum \"{0}\" setzen"},
         { " dag: " , " Datum: " },
         { "tijd:", "Zeit:" },
         // resultLoogger 
         { "Overzicht Logs", "Übersicht Protokolle" },
         { "deel-scores", "Teilergebnisse" },
         { "tijdsduur", "Länge" },
         // default partial score
         { "resultaat", "Ergebnis" },
         //importexportdialog
         {"Kopiëer modules", "Module kopieren" }, 
         {"Toestaan", "Erlauben" },
         {"Modules beschikbaar stellen", "Module zur Verfügung stellen" },
         {"Modules opvragen", "Module abrufen"},
         {"Delen met","Teilen mit"},
         {"Alle scholen","Alle Schulen"},
         {"Scholen", "Schulen"},
         {"toepassen", "Anwenden"},
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Schule auswählen<br>" +
			   "(2) Gegebenenfalls Vorschau der Module aufrufen<br>" +
			   "(3) Eins oder mehrere Module für die eigene Schule auswählen<br><br>" +
			   "Die ausgewählten Module werden in ihre eigene Modulansicht kopiert<br>"+
			   "und können in der eigenen Schule benutzt werden." },
		  { TextMapper.GUIEID_MSG2, "<html>Ich möchte mitmachen und in der Liste der teilnehmenden Schulen aufgeführt werden"},
		  { TextMapper.GUIEID_MSG3, "<html>(1) Module auswählen<br>(2) Schulen auswählen<br><br>Die ausgewählten Module stehen<br>den ausgewählten Schulen zur Verfügung." },

};

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}