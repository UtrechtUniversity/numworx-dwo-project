//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_nl.java

package fi.dwo.client.system.text;

import java.util.ListResourceBundle;

import fi.dwo.client.system.TextMapper;

public class Text_nl extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Gast"}, 
         { TextMapper.BTN_LOGIN, "Login" },
         { TextMapper.BTN_CANCEL, "Annuleren" },
         { TextMapper.BTN_NO, "Nee" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Ja" },
         { TextMapper.BTN_CLOSE, "Sluiten"}, 
         { TextMapper.DLG_CONFIRM, "Bevestiging" },
         { TextMapper.DLG_ENTER_INPUT, "Voer in" },
         { TextMapper.DLG_MESSAGE, "Bericht" },

         { TextMapper.EX_UNKNOWN_ERROR, "Er is een interne fout opgetreden" },
         { TextMapper.EXR_USER_EXISTS, "De opgegeven gebruikersnaam bestaat al" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "De opgegeven wachtwoorden komen niet overeen" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Geen gebruiker gevonden met opgegeven gebruikersnaam en wachtwoord" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Onbekende school/groep/wachtwoord combinatie" },
         { TextMapper.EXR_MANDATORY, "{0} bij {1} is niet ingevuld. Deze is verplicht"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} bij {1} bevat illegale tekens." },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} bij {1} is niet goed." },
         { TextMapper.EXC_CLASS_EXISTS, "Er bestaat al een klas met de opgegeven naam" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Er bestaat al een school met de opgegeven schoollogin" },
         { TextMapper.EXL_UNKNOWN_USER, "De opgegeven gebruikersnaam en wachtwoord komen niet overeen" },
         { TextMapper.EXC_COURSE_EXISTS, "Er bestaat al een module met de opgegeven naam" },
         { TextMapper.EXS_SCO_EXISTS, "Er bestaat al een activiteit met de opgegeven naam" },
         { TextMapper.EXS_NO_APPLET, "Applet niet gevonden"},
         
         { TextMapper.GUI_WAIT_A_MOMENT, "Een moment geduld aub"}, 

         { TextMapper.GUIW_LOGINDATA, "Inloggegevens" },
         { TextMapper.GUIW_USERNAME, "Gebruikersnaam" },
         { TextMapper.GUIW_PASSWORD, "Wachtwoord" },
         { TextMapper.GUIW_WELCOME, "Welkom" },
         { TextMapper.GUIW_GUESTLOGIN, "Inloggen als gast" },
         { TextMapper.GUIW_REGISTER, "Aanmelden" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Uw werk wordt NIET opgeslagen!" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "Meld u aan als nieuwe gebruiker." },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Login als Gast" },
         { TextMapper.GUIW_BTN_LOGIN, "Inloggen" },
         { TextMapper.GUIW_BTN_REGISTER, "Aanmelden" },
         { TextMapper.GUIW_ERR_LOGIN, "Fout bij het Inloggen" },

         { TextMapper.GUIR_REGISTER, "Aanmelden"},
         
         { TextMapper.GUIR_REGISTERINFO, "Kies zelf een naam en wachtwoord"},
         { TextMapper.GUIR_PERSONALINFO, "Persoonlijke Gegevens"},
         { TextMapper.GUIR_SCHOOLINFO, "School Gegevens"},

         { TextMapper.GUIR_USERNAME, "Gebruikersnaam"},
         { TextMapper.GUIR_PASSWORD, "Wachtwoord"},
         { TextMapper.GUIR_RE_PASSWORD, "Bevestig Wachtwoord"}, 

         { TextMapper.GUIR_FIRSTNAME, "Voornaam"},
         { TextMapper.GUIR_MIDDLENAME, "Tussenvoegsel"}, 
         { TextMapper.GUIR_LASTNAME, "Achternaam"}, 
         { TextMapper.GUIR_EMAIL, "E-mail adres"},

         { TextMapper.GUIR_SCHOOLLOGIN, "Schoollogin"},
         { TextMapper.GUIR_SCHOOLGROUP, "Ik ben"}, 
         { TextMapper.GUIR_SCHOOLPASSWORD, "Sleutelcode"},
         
         { TextMapper.GUIR_BTN_REGISTER, "Aanmelden"}, 
         { TextMapper.GUIR_BTN_RESET, "Alles Wissen"},
         { TextMapper.GUIR_BTN_BACK, "Terug naar Hoofdmenu"},

         { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Gegevens verstrekt door de school"},

         { TextMapper.GUIR_OPT_SELECT_GROUP, "Zonder school"},
         { TextMapper.GUIR_OPT_STUDENT, "Leerling"},
         { TextMapper.GUIR_OPT_TEACHER, "Docent"},
         { TextMapper.GUIR_OPT_ADMIN, "Administrator"},
         { TextMapper.GUIR_OPT_SCHOOLCODE, "Sleutelcode"},
         { TextMapper.GUIR_OPT_SCHOOLADMIN, "Schooladmin" },
         { TextMapper.GUIR_ERR_REGISTER, "Fout bij het Aanmelden" },
         
         { TextMapper.GUIR_MSG_REGISTERED, "U bent succesvol geregistreerd" },

         { TextMapper.GUIM_DWO_FULL, "Digitale Wiskunde Omgeving"},
         { TextMapper.GUIM_DWO_SHORT, "DWO"},
         { TextMapper.GUIM_FI_NAME, "Freudenthal Instituut"},
         { TextMapper.GUIM_MAIN_MENU, "Modules"},
 
         { TextMapper.GUIL_LOGGED_IN_AS, "Ingelogd"},
         { TextMapper.GUIL_NOT_LOGGED_IN, "Niet ingelogd"},
         { TextMapper.GUIL_BTN_LOGIN, "Inloggen"},
         { TextMapper.GUIL_BTN_LOGOFF, "Uitloggen"},

         { TextMapper.GUIMNU_MAIN_MENU, "Overzicht modules"},
         { TextMapper.GUIMNU_MY_PROFILE, "Mijn Profiel"},    
         { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Leerling van klas"},    
         { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Je bent nog niet "},
         { TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "aangemeld bij een  "},
         { TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "klas. Ga naar"},
         { TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Mijn Profiel\" en"},
         { TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "kies een klas."},
         { TextMapper.GUIMNU_CLASS_RESULTS, "Resultaten van klas"},      
         { TextMapper.GUIMNU_RESULTS, "Resultaten bekijken"},
         { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Klassen beheren"},
         { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Scholen beheren"},
         { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Modules beheren"},
         { TextMapper.GUIMNU_MSG_ADD_CLASS, "Geef de naam van de nieuwe klas"},   
         { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Nieuwe klas aanmaken"},
         { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Geef de naam van de nieuwe school"},   
         { TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Nieuwe school aanmaken"},
         
         { TextMapper.GUICO_HEADER, "Modules"}, 
         { TextMapper.GUICO_SCO_LIST_TITLE, "Activiteiten"},

         { TextMapper.GUIP_MY_PROFILE, "Mijn Profiel"}, 
         { TextMapper.GUIP_REGISTERINFO, "Registratie Informatie"},
         { TextMapper.GUIP_PERSONALINFO, "Persoonlijke Gegevens"}, 
         { TextMapper.GUIP_SCHOOLINFO, "School Gegevens"}, 

         { TextMapper.GUIP_USERNAME, "Gebruikersnaam"}, 
         { TextMapper.GUIP_OLD_PASSWORD, "Huidig Wachtwoord"}, 
         { TextMapper.GUIP_PASSWORD, "Nieuw Wachtwoord"}, 
         { TextMapper.GUIP_RE_PASSWORD, "Bevestig Wachtwoord"}, 

         { TextMapper.GUIP_FIRSTNAME, "Voornaam"}, 
         { TextMapper.GUIP_MIDDLENAME, "Tussenvoegsel"}, 
         { TextMapper.GUIP_LASTNAME, "Achternaam"}, 
         { TextMapper.GUIP_EMAIL, "E-mail adres"}, 

         { TextMapper.GUIP_SCHOOLLOGIN, "Schoollogin"}, 
         { TextMapper.GUIP_SCHOOLGROUP, "Ik ben"}, 
         { TextMapper.GUIP_SCHOOLPASSWORD, "Wachtwoord"},
         { TextMapper.GUIP_CLASS, "klas"}, 
         
         { TextMapper.GUIP_BTN_SAVE, "Opslaan"},
         { TextMapper.GUIP_BTN_RESET, "Reset"},
         { TextMapper.GUIP_BTN_DELETE_PROFILE, "Profiel Verwijderen"},

         { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Gegevens verstrekt door de school"},
         
         { TextMapper.GUIP_ERR_CHANGE, "Fout bij het wijzigen van het register" },
         
         { TextMapper.GUIP_OPT_SELECT_GROUP, "Maak een keuze"},
 
         { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Weet u zeker dat u uw account wilt verwijderen"},
         { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Account verwijderen"},
         
         { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Uw account is succesvol gewijzigd"}, 

         { TextMapper.GUIPT_SCHOOL, "School"},
         { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Docent van Klas"},
         { TextMapper.GUIPT_BTN_ADD_CLASS, "Klas Maken"},
         
         { TextMapper.GUIS_STUDENTS, "Leerlingen"},
         { TextMapper.GUIS_TEACHERS, "Docenten"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Scholen Beheren"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "School {0} verwijderen"},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Wijzig Schoolnaam"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Leerlingen van {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "School aanmaken"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Verwijder school"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Wijzig schoolnaam"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Geef de nieuwe naam van de school"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Weet u zeker dat u de school wilt verwijderen"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "De school bevat nog gebruikers. Weet u zeker dat u de school wilt verwijderen"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "School bevat nog gebruikers"},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Weet u zeker dat u {0} uit de school wilt verwijderen"},
         { TextMapper.GUIS_DELETE_STUDENT, "Leerling uit school verwijderen"},
         { TextMapper.GUIS_NO_STUDENTS, "School {0} bevat geen leerlingen"}, 
         
         { TextMapper.GUIC_STUDENTS, "Leerlingen"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Klassen Beheren"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Klas {0} verwijderen"},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Wijzig klasnaam"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Leerlingen in klas {0}"},
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Modules toekennen aan klas {0}"},

         { TextMapper.GUIC_ADD_CLASS, "Klas aanmaken"},
         { TextMapper.GUIC_DELETE_CLASS, "Verwijder klas"},
         { TextMapper.GUIC_RENAME_CLASS, "Wijzig klasnaam"}, 
         { TextMapper.GUIC_MSG_RENAME_CLASS, "Geef de nieuwe naam van de klas"},
         { TextMapper.GUIC_MSG_DELETE_CLASS, "Weet u zeker dat u de klas wilt verwijderen"},
         { TextMapper.GUIC_CLASS_NOT_EMPTY, "De klas bevat nog leerlingen. Weet u zeker dat u de klas wilt verwijderen"},
         { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Klas bevat nog leerlingen"},
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Weet u zeker dat u {0} uit de klas wilt verwijderen"},
         { TextMapper.GUIC_DELETE_STUDENT, "Leerling uit klas verwijderen"},
         { TextMapper.GUIC_NO_STUDENTS, "Klas {0} bevat geen leerlingen"}, 
         
         { TextMapper.GUIRS_RESULTS, "Resultaten"},
         { TextMapper.GUIRS_NO_RESULTS, "Geen resultaten om weer te geven"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Selecteer Modules"},
         
         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Kies hier de modules waarvan u de resultaten wilt bekijken"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Resultaten van {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Sorteren op {0}"},
         
         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Resultaten van {0} bij \"{1}\""},
         
         { TextMapper.UG_RESULTS_OF_STUDENT, "Resultaten van de Activiteit \"{0}\" van {1}"},
         
         { TextMapper.GUISC_TITLE, "Selecteer Modules"}, 
         { TextMapper.GUISC_BTN_SELECT_ALL, "Alles selecteren"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Alles deselecteren"},

         { TextMapper.UG_CLASSES, "Klassen"},
         //{ TextMapper.UG_STUDENTS_OF_CLASS, "Leerlingen van {0}"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Klas {0}"},
         
         { TextMapper.UG_USER_TITLE,"Leerling"}, 
         { TextMapper.UG_CLASS_TITLE,"Klas"},

         { TextMapper.UG_CLASS_CHILD, "leerlingen {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "klasnaam (A-Z)"}, 
         { TextMapper.UG_CLASS_ORDER_DESC, "klasnaam (Z-A)"},
         
         { TextMapper.UG_USER_PARENT, "klassen"},
         { TextMapper.UG_USER_ORDER_ASC, "achternaam (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "achternaam (Z-A)"},

         { TextMapper.LG_COURSES, "Modules"},
         //{ TextMapper.LG_SCOS_OF_COURSE, "Activiteiten van \"{0}\""},
         { TextMapper.LG_SCOS_OF_COURSE, "Module \"{0}\""},

         { TextMapper.LG_COURSE_CHILD, "activiteiten van \"{0}\""},
         { TextMapper.LG_COURSE_ORDER_ASC, "resultaat (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "resultaat (100-0)"},
         
         { TextMapper.LG_SCO_PARENT, "modules"},
         { TextMapper.LG_SCO_ORDER_ASC, "resultaat (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "resultaat (100-0)"},
         
         { TextMapper.LG_SCO_NAME , "Activ. {0}"},
         
         { TextMapper.GUIC_ADD_COURSE, "Nieuwe Module aanmaken"},
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Modules beheren"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Module \"{0}\" verwijderen"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Wijzig module"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Activiteiten beheren"},

         { TextMapper.GUICDLG_COURSE_NAME, "Module naam"},
         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Module beschrijving"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Nieuwe module aanmaken"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Module wijzigen"},
         
         { TextMapper.GUIC_NO_COURSES, "Geen modules aanwezig"}, 
         
         { TextMapper.GUIC_MSG_COURSE_DELETE, "Deze module bevat nog SCO`s. \nWanneer u deze module verwijdert, zullen de SCO`s \nen de resultaten van deze SCO`s verwijderd worden.\n \nWeet u zeker dat u deze module wilt verwijderen?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Weet u zeker dat u deze module wilt verwijderen?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Module verwijderen"},

         { TextMapper.GUIS_ADD_SCO, "Nieuwe Activiteit aanmaken"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Activiteiten van Module \"{0}\""},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Activiteiten beheren"},

         { TextMapper.GUIS_TLTP_DELETE_SCO, "Activiteit \"{0}\" verwijderen"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Wijzig de naam van de activiteit"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Activiteit bewerken"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Terug naar modules"},

         { TextMapper.GUISDLG_SCO_NAME, "Activiteit naam"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Activiteit beschrijving"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Nieuwe Activiteit aanmaken"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Activiteit wijzigen"},
         
         { TextMapper.GUIS_MSG_SCO_DELETE, "Wanneer u deze activiteit wilt verwijderen,\nworden ook de resultaten verwijderd.\n \nWeet u zeker dat u deze activiteit wilt verwijderen?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Activiteit verwijderen"},
         { TextMapper.GUIS_NO_SCOS, "Module {0} bevat geen activiteiten"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Toevoegen"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Bekijk Activiteit"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Kies Activiteit"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "Geen activiteiten om toe te voegen"},
         { TextMapper.GUISDLG_SHOW, "Toon"},
         { TextMapper.GUISDLG_ALL, "Alles"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "Er is geen activiteit geselecteerd"},
         
         { TextMapper.GUIPA_BTN_PREVIEW, "Preview"},
         { TextMapper.GUIPA_BTN_SAVE, "Opslaan"},
         { TextMapper.GUIPA_BTN_RESET, "Reset"},
         { TextMapper.GUIPA_BTN_CANCEL, "Annuleren"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Activiteit bewerken"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Deze activiteit kan niet worden aangepast"},
         
         { TextMapper.GUIPA_DLG_TTL, "Edit-Mode van Activiteit \"{0}\""},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Wanneer u deze nieuwe instellingen opslaat,\ndan worden de resultaten van de oude opdrachten verwijderd.\n \nWeet u zeker dat u deze instellingen wilt opslaan?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Instellingen opslaan"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Parameters ({0})"}
};

 public Text_nl() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}