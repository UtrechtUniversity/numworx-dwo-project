//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.commons.system.text;

import java.util.ListResourceBundle;

import fi.dwo.commons.system.TextMapper;

public class Text_en extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Guest"},
     
         /* General word constants */
         { TextMapper.LBL_USERNAME, "Username" },
         { TextMapper.LBL_PASSWORD, "Password" },
     
         /* General button constants */
         { TextMapper.BTN_LOGIN, "Login" },
         { TextMapper.BTN_CANCEL, "Cancel" },
         { TextMapper.BTN_NO, "No" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Yes" },
         { TextMapper.BTN_CLOSE, "Close"},
         { TextMapper.DLG_CONFIRM, "Confirm" },
         { TextMapper.DLG_ENTER_INPUT, "Enter input" },
         { TextMapper.DLG_MESSAGE, "Message" },

         { TextMapper.EX_UNKNOWN_ERROR, "An internal error occured" },
         { TextMapper.EXR_USER_EXISTS, "The specified username already exists" },
         { TextMapper.EXR_USER_EXISTS2, "The username {0} already exists" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "The specified passwords are different" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "An user with the specified username and password was not found" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Unknown school/group/password combination" },
         { TextMapper.EXR_MANDATORY, "{0} at {1} isn't filled. This is a required field"},
         { TextMapper.EXR_WRONG_FORMAT, "{0} at {1} contains illegal characters" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "{0} at {1} is illegal" },
         { TextMapper.EXC_CLASS_EXISTS, "The specified class already exists" },
         { TextMapper.EXS_SCHOOL_EXISTS, "A school with the specified schoollogin already exists" },
         { TextMapper.EXL_UNKNOWN_USER, "An user with the specified username and password was not found" },
         { TextMapper.EXC_COURSE_EXISTS, "The specified module already exists" },
         { TextMapper.EXS_SCO_EXISTS, "The specified Activity already exists" },
         { TextMapper.EXS_NO_APPLET, "Applet not found"},

         { TextMapper.GUI_WAIT_A_MOMENT, "One moment please"},

        /* Reauthenticate Panel */
         { TextMapper.GUIREAUTH_AREYOUSURE, "<HTML>Are you absolutely sure you want to delete your account, all profiles and all data?<BR> If so, then enter your password and click OK.</HTML>"}, 
         
         { TextMapper.GUIW_LOGINDATA, "Login data" },
         { TextMapper.GUIW_USERNAME, "Username" },
         { TextMapper.GUIW_PASSWORD, "Password" },
         { TextMapper.GUIW_WELCOME, "Welcome" },
         { TextMapper.GUIW_GUESTLOGIN, "Login as guest" },
         { TextMapper.GUIW_REGISTER, "Register for an account" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Your work WON'T be saved" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "New account." },
         { TextMapper.GUIW_MSG_REGISTER_EXISTING, "Existing account." },//
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Login as Guest" },
         { TextMapper.GUIW_BTN_LOGIN, "Login" },
         { TextMapper.GUIW_BTN_REGISTER, "Register for an account or a school" },
         { TextMapper.GUIW_ERR_LOGIN, "Login error" },

         { TextMapper.GUIR_REGISTER, "Register" },

         { TextMapper.GUIR_REGISTERINFO, "New User registration" },
         { TextMapper.GUIR_PERSONALINFO, "Personal information" },
         { TextMapper.GUIR_SCHOOLINFO, "School information" },

         { TextMapper.GUIR_USERNAME, "Username" },
		 { TextMapper.GUIR_PASSWORD, "Password" },
		 { TextMapper.GUIR_RE_PASSWORD, "Confirm password" },

		 { TextMapper.GUIR_FIRSTNAME, "Name" },
		 { TextMapper.GUIR_MIDDLENAME, "Middlename" },
		 { TextMapper.GUIR_LASTNAME, "Family name" },
		 { TextMapper.GUIR_EMAIL, "E-mail adres" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "Schoollogin" },
		 { TextMapper.GUIR_SCHOOLGROUP, "I am" },
		 { TextMapper.GUIR_SCHOOLPASSWORD, "Password" },

		 { TextMapper.GUIR_BTN_REGISTER, "Register" },
		 { TextMapper.GUIR_BTN_RESET, "Reset" },
         { TextMapper.GUIR_BTN_BACK, "Back to modules" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Data provided by the school" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "Make a choice" },
            { TextMapper.GUIR_OPT_STUDENT, "Student" },
            { TextMapper.GUIR_OPT_TEACHER, "Teacher" },
            { TextMapper.GUIR_OPT_NOSCHOOL, "No school" },
            { TextMapper.GUIR_OPT_ADMIN, "Administrator"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "School admin" },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Key code"},
            
            { TextMapper.GUIR_ERR_REGISTER, "An error has occured" },

            { TextMapper.GUIR_MSG_REGISTERED, "You are successfully registered"},

            { TextMapper.GUIM_DWO_FULL, "Digital Mathematics Environment" },
            { TextMapper.GUIM_DWO_SHORT, "DME" },
            { TextMapper.GUIM_FI_NAME, "Freudenthal Institute"},
            { TextMapper.GUIM_MAIN_MENU, "Modules" },

            { TextMapper.GUIL_LOGGED_IN_AS, "You're logged in as" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "You are not logged in"},
            { TextMapper.GUIL_BTN_LOGIN, "Login"},
            { TextMapper.GUIL_BTN_LOGOFF, "Logout" },

            { TextMapper.GUIMNU_MAIN_MENU, "Modules" },
            { TextMapper.GUIMNU_MY_PROFILE, "My account" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Student of class" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "You are not yet "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "member of a class.  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Go to"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"My profile\" and "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "choose a class."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Results of class" },
            { TextMapper.GUIMNU_RESULTS, "Look at results" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Class management" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "School management" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Module management"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Name of the new class" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Add new class" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Name of the new school"},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Add new school"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "School users"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "School classes"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Features schooladmin"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Manage users"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Remove only from school"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Remove complete account"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Add new students"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Add new teachers"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Import from clipboard"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Make accounts"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Additional row"},

         	{ TextMapper.GUICO_HEADER, "Modules"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "Activities"},

            { TextMapper.GUIP_MY_PROFILE, "My profile" },
            { TextMapper.GUIP_REGISTERINFO, "Registration information" },
            { TextMapper.GUIP_PERSONALINFO, "Personal information" },
            { TextMapper.GUIP_SCHOOLINFO, "School information" },
            { TextMapper.GUIP_ACCOUNTANDCONTACTINFO, "Account and contact information"},

            { TextMapper.GUIP_USERNAME, "Username" },
            { TextMapper.GUIP_OLD_PASSWORD, "Current Password" },
            { TextMapper.GUIP_PASSWORD, "New Password" },
            { TextMapper.GUIP_RE_PASSWORD, "Confirm Password" },

            { TextMapper.GUIP_FIRSTNAME, "Firstname" },
            { TextMapper.GUIP_MIDDLENAME, "Middlename" },
            { TextMapper.GUIP_LASTNAME, "Name" },
            { TextMapper.GUIP_EMAIL, "E-mail adres" },

            { TextMapper.GUIP_SCHOOLLOGIN, "Schoollogin" },
            { TextMapper.GUIP_SCHOOLGROUP, "I am" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "Password" },
            { TextMapper.GUIP_CLASS, "Class" },

            { TextMapper.GUIP_BTN_EDIT, "Edit" },
            { TextMapper.GUIP_BTN_SAVE, "Save" },
            { TextMapper.GUIP_BTN_RESET, "Reset" },
            { TextMapper.GUIP_BTN_SWITCH_PROFILE, "Switch to selected profile"},
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Delete profile" },
            { TextMapper.GUIP_BTN_DELETE_ACCOUNT, "Delete account" },
            { TextMapper.GUIP_BTN_ADD_ROLE, "New login option"},
            { TextMapper.GUIP_ROLE_OPTIONS, "Login options"},

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Data provided by the school" },

            { TextMapper.GUIP_ERR_CHANGE, "An error occured" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "Make a choice" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Are you sure that you want to delete your account" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Delete Account" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Your account is successfully changed"},

            { TextMapper.GUIPT_SCHOOL, "School" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Teacher of class" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Add class" },
            
         { TextMapper.GUIS_STUDENTS, "Students"},
         { TextMapper.GUIS_TEACHERS, "Teachers"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "School management"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Delete school {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Edit Schoolname"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Students of {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Add school"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Delete school"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Edit schoolname"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Enter a new school name"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Are you sure you want to delete this school"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "This school contains users. Are you sure you want to delete this school"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "This school contains users."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Are you sure you want to delete {0} from this school"},
         { TextMapper.GUIS_DELETE_STUDENT, "Delete students from this school"},
         { TextMapper.GUIS_NO_STUDENTS, "School {0} doesn't contains students"}, 

         { TextMapper.GUIC_STUDENTS, "Students"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Class management"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Delete class {0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Edit class name"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Students in class {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Assign module to class {0}"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "Students" },
            { TextMapper.GUIC_ADD_CLASS, "Create class"},
            { TextMapper.GUIC_DELETE_CLASS, "Delete class" },
            { TextMapper.GUIC_RENAME_CLASS, "Edit classname" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "New name of the class" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "Are you sure you want to delete the class" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "There are some students in the class. Are you sure you want to delete the class" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "There are some students in the class" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Are you sure you want to delete {0} from the class"},
         { TextMapper.GUIC_DELETE_STUDENT, "Delete student from class"},
         { TextMapper.GUIC_NO_STUDENTS, "There are no students in class {0}"},

         { TextMapper.GUIRS_RESULTS, "Results"},
         { TextMapper.GUIRS_NO_RESULTS, "There are no results"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Select Modules"},
         { TextMapper.GUIRS_BTN_COPY_TO_CLIPBOARD, "Copy to Clipboard"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Select a module"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Results from {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Sort on {0}"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Show results of Activity {0} of {1}"},
         { TextMapper.GUIRSDLG_MSG, "Delete all results of ''{0}'' for {1}?"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "Results of Activity {0} of {1}"},

         { TextMapper.GUISC_TITLE, "Select modules"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Select all"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Deselect all"},

         { TextMapper.UG_CLASSES, "Classes"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Students of {0}"},

         { TextMapper.UG_USER_TITLE,"Student"},
         { TextMapper.UG_CLASS_TITLE,"Class"},

         { TextMapper.UG_CLASS_CHILD, "Students {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Classname (A-Z)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Classname (Z-A)"},

         { TextMapper.UG_USER_PARENT, "Classes"},
         { TextMapper.UG_USER_ORDER_ASC, "lastname (A-Z)"},
         { TextMapper.UG_USER_ORDER_DESC, "lastname (Z-A)"},

         { TextMapper.LG_COURSES, "Modules"},
         { TextMapper.LG_SCOS_OF_COURSE, "Activities of {0}"},

         { TextMapper.LG_COURSE_CHILD, "Activities of {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "results (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "results (100-0)"},

         { TextMapper.LG_SCO_PARENT, "modules"},
         { TextMapper.LG_SCO_ORDER_ASC, "results (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "results (100-0)"},

         { TextMapper.LG_SCO_NAME , "Activity {0}"},

         { TextMapper.GUIC_ADD_COURSE, "Add new module"},
         { TextMapper.GUIC_ADD_MAP, "Add new folder" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Module management"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Delete module {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Delete folder {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Edit module"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Activity management"},

         { TextMapper.GUICDLG_COURSE_NAME, "Module name"},
         { TextMapper.GUICDLG_MAP_NAME, "Folder name" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Description"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Add new module"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Edit module"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Edit folder" },

         { TextMapper.GUIC_NO_COURSES, "There are no modules to show"},
         { TextMapper.GUIC_COURSE_SHARE, "Share modules" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "There are Activities present. \nWhen you delete the module \nen the results of the Activities will also be deleted.\n \nAre you sure you want to delete the module?"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Are you sure you want to delete the module?"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Delete module"},

         { TextMapper.GUIS_ADD_SCO, "Add new Activity"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Activities of module {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Activity management"},
         { TextMapper.GUIS_SHOW_SCORE, "Students see their result"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "Delete Activity {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Edit name Activity"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Edit Activity"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Back to modules"},

         { TextMapper.GUISDLG_SCO_NAME, "Activity name"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Activity description"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Add new Activity"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Edit Activity"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "When you delete the Activity \nen the results will also be deleted.\n \nAre you sure you want to delete the Activity?"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Delete Activity"},
         { TextMapper.GUIS_NO_SCOS, "There are no Activities in module {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "Load icon of {0}"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Add"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Preview Activity"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Choose Activity"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "There are no Activities to add"},
         { TextMapper.GUISDLG_SHOW, "Show"},
         { TextMapper.GUISDLG_ALL, "All"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "You haven't selected any Activities"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Standard activities"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Own activities"},

         { TextMapper.GUIPA_BTN_PREVIEW, "Preview Activity"},
         { TextMapper.GUIPA_BTN_SAVE, "Save"},
         { TextMapper.GUIPA_BTN_RESET, "Reset"},
         { TextMapper.GUIPA_BTN_CANCEL, "Close"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Edit Activity"},
         
         { TextMapper.GUIPA_NO_PARAMS, "This activity can't be changed"},

         { TextMapper.GUIPA_DLG_TTL, "Edit-Mode of Activity {0}"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "If you save this new configuration,\nthe result of the older items will be removed\n \nAre you sure you want to save this configuration?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Save configuration"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Parameters ({0})"},
         
         { "cut", "Cut" },
         { "copy" , "Copy" },
         { "paste", "Paste"},
         { "delete", "Delete" },
         { "edit", "Edit" },
         { "file", "File" },
         { "rename", "Rename" },

         { TextMapper.GUIA_INSERT_SCOS, "Insert activities from backup"},
         { TextMapper.GUIH_STOP_EDIT, "Stop editing" },
         { TextMapper.GUIH_EDIT, "Edit" },
         
         { "Alle modules", "All modules"},
         { "Standaard DWO modules", "Standard DME modules"},

         { "Nieuwe Modulemap", "New Module folder" },

         // classadminpanel
         { "Klassen toewijzen", "Assign classes" },
         { "Klas", "Class" },
         { "Docent", "Teacher" },
         { "Verwijder", "Remove" },
         // classpanel 
         { "boomstructuur?", "treeview?" },
         // select courses dialog
         { "Leerlinggegevens verwijderen", "Remove studentresults" },
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "Do you wish to remove all result of {0} for {1}?" },
         { "soort", "kind" },
         { "vanaf", "from" },
         { "tot aan", "until" },
         { "tot", "until" },
         { "Ll ggvns", "results" },
         { "normaal", "normal" },
         { "afgeschermd", "secured" },
         { "Geef tijdstip {0}", "Set date and time \"{0}\""},
         { " dag: " , " date: " },
         { "tijd:", "time:" },
         // resultLoogger 
         { "Overzicht Logs", "Overview Logs" },
         { "deel-scores", "partial scores" },
         { "tijdsduur", "duration" },
         // default partial score
         { "resultaat", "result" },
         //importexportdialog
         {"Kopiëer modules", "Copy modules" }, 
         {"Toestaan", "Allow" },
         {"Modules beschikbaar stellen", "Provide modules" },
         {"Modules opvragen", "Request modules"},
         {"Delen met","Share with"},
         {"Alle scholen","All schools"},
         {"Scholen", "Schools"},
         {"toepassen", "Apply"},
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Select a school<br>" +
			   "(2) Eventually preview the shown modules<br>" +
			   "(3) Select one or more modules for use in your own school<br><br>" +
			   "The selected modules are copied to your own module view<br>"+
			   "and can be used at your own school." },
	     { TextMapper.GUIEID_MSG2, "<html>I wish to participate in this way of sharing and become visible as school in the lists"},
		 { TextMapper.GUIEID_MSG3, "<html>(1) Select modules<br>(2) Select schools<br><br>The selected modules are available<br>to the selected schools." },

		 { "leerlingen ook", "students too"},
		 { TextMapper.GUIUMP_REMOVE_CLASS, "Remove all students from {0}?" },
		 { TextMapper.GUIUMP_ALL_STUDENTS, "all students" },
		 { TextMapper.GUIC_SETTINGS, "Configuration of {0}" },
		 { TextMapper.GUIH_SETTINGS, "School configuration" },
			
		 { TextMapper.GUIC_SETTINGS_STUDENT, "Students choose their own class"},
		 { TextMapper.GUIC_SETTINGS_TEACHER, "Teachers choose the class of their students"},
		 { TextMapper.GUIC_SETTINGS_MODULE, "Teachers are allowed to change modules"},
 // google translate...
		 { TextMapper.GUICDLG_LICENCE, "The subscription for ''{0}'' has expired!\nNew subscribers can no longer be added.\nRefer to the contact person for the DME subscription at school." },

		 { TextMapper.DWOAPPLET_EXISTS, "There is already a DME!"},

 };

 public Text_en() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}