package fi.dwo.client.system.text;

import java.util.ListResourceBundle;

import fi.dwo.client.system.TextMapper;

public class Text_gr extends ListResourceBundle {
 private final Object[][] contents = {
         { TextMapper.USER_GUEST, "Επισκέπτης"},
         { TextMapper.BTN_LOGIN, "Σύνδεση" },
         { TextMapper.BTN_CANCEL, "Άκυρο" },
         { TextMapper.BTN_NO, "Όχι" },
         { TextMapper.BTN_OK, "OK" },
         { TextMapper.BTN_YES, "Ναι" },
         { TextMapper.BTN_CLOSE, "Κλείσιμο"},
         { TextMapper.DLG_CONFIRM, "Επιβεβαίωση" },
         { TextMapper.DLG_ENTER_INPUT, "Εισαγωγή δεδομένων" },
         { TextMapper.DLG_MESSAGE, "Μήνυμα" },

         { TextMapper.EX_UNKNOWN_ERROR, "Προέκυψε ένα εσωτερικό σφάλμα" },
         { TextMapper.EXR_USER_EXISTS, "Το συγκεκριμένο όνομα χρήστη υπάρχει ήδη" },
         { TextMapper.EXR_USER_EXISTS2, "Το όνομα χρήστη {0} υπάρχει ήδη" },
         { TextMapper.EXR_WRONG_SECOND_PASSWORD, "Οι συγκεκριμένοι κωδικοί πρόσβασης είναι διαφορετικοί" },
         { TextMapper.EXR_WRONG_USERNAME_PASSWORD, "Δεν βρέθηκε χρήστης με το συγκεκριμένο όνομα και κωδικό πρόσβασης" },
         { TextMapper.EXR_UNKNOWN_SCHOOLGROUP, "Άγνωστος συνδυασμός σχολείου/ομάδας/κωδικού πρόσβασης" },
         { TextMapper.EXR_MANDATORY, "Το {0} στο {1} δεν έχει συμπληρωθεί. Αυτό είναι ένα υποχρεωτικό πεδίο"},
         { TextMapper.EXR_WRONG_FORMAT, "Το {0} στο {1} συμπεριλαμβάνει μη αποδεκτούς χαρακτήρες" },
         { TextMapper.EXR_WRONG_EMAILFORMAT, "Το {0} στο {1} είναι μη αποδεκτό" },
         { TextMapper.EXC_CLASS_EXISTS, "Η συγκεκριμένη τάξη υπάρχει ήδη" },
         { TextMapper.EXS_SCHOOL_EXISTS, "Υπάρχει ήδη ένα σχολείο με αυτά τα στοιχεία σύνδεσης" },
         { TextMapper.EXL_UNKNOWN_USER, "Δεν βρέθηκε χρήστης με το συγκεκριμένο όνομα και κωδικό" },
         { TextMapper.EXC_COURSE_EXISTS, "Το συγκεκριμένο module υπάρχει ήδη" },
         { TextMapper.EXS_SCO_EXISTS, "Η συγκεκριμένη Δραστηριότητα υπάρχει ήδη" },
         { TextMapper.EXS_NO_APPLET, "Δεν βρέθηκε το Applet"},

         { TextMapper.GUI_WAIT_A_MOMENT, "Μια στιγμή παρακαλώ"},

         { TextMapper.GUIW_LOGINDATA, "Στοιχεία σύνδεσης" },
         { TextMapper.GUIW_USERNAME, "Όνομα Χρήστη" },
         { TextMapper.GUIW_PASSWORD, "Κωδικός" },
         { TextMapper.GUIW_WELCOME, "Καλώς ήλθατε" },
         { TextMapper.GUIW_GUESTLOGIN, "Σύνδεση ως επισκέπτης" },
         { TextMapper.GUIW_REGISTER, "Εγγραφή" },
         { TextMapper.GUIW_MSG_WORK_NOT_SAVE, "Η εργασία σας ΔΕΝ έχει αποθηκευτεί" },
         { TextMapper.GUIW_MSG_REGISTER_NEW, "Εγγραφή ως νέο μέλος." },
         { TextMapper.GUIW_BTN_GUESTLOGIN, "Σύνδεση ως Επισκέπτης" },
         { TextMapper.GUIW_BTN_LOGIN, "Σύνδεση" },
         { TextMapper.GUIW_BTN_REGISTER, "Εγγραφή" },
         { TextMapper.GUIW_ERR_LOGIN, "Σφάλμα σύνδεσης" },

         { TextMapper.GUIR_REGISTER, "Εγγραφή" },

         { TextMapper.GUIR_REGISTERINFO, "Εγγραφή νέου χρήστη" },
         { TextMapper.GUIR_PERSONALINFO, "Προσωπικά στοιχεία" },
         { TextMapper.GUIR_SCHOOLINFO, "Στοιχεία σχολείου" },

         { TextMapper.GUIR_USERNAME, "Όνομα Χρήστη" },
		 { TextMapper.GUIR_PASSWORD, "Κωδικός" },
		 { TextMapper.GUIR_RE_PASSWORD, "Επαλήθευση κωδικού" },

		 { TextMapper.GUIR_FIRSTNAME, "Όνομα" },
		 { TextMapper.GUIR_MIDDLENAME, "Δεύτερο όνομα" },
		 { TextMapper.GUIR_LASTNAME, "Επώνυμο" },
		 { TextMapper.GUIR_EMAIL, "Διεύθυνση E-mail" },

		 { TextMapper.GUIR_SCHOOLLOGIN, "Σύνδεση Σχολείου" },
		 { TextMapper.GUIR_SCHOOLGROUP, "Είμαι" },
		 { TextMapper.GUIR_SCHOOLPASSWORD, "Κωδικός" },

		 { TextMapper.GUIR_BTN_REGISTER, "Εγγραφή" },
		 { TextMapper.GUIR_BTN_RESET, "Επαναφορά" },
         { TextMapper.GUIR_BTN_BACK, "Πίσω στα modules" },

            { TextMapper.GUIR_MSG_PROVIDED_SCHOOL, "Στοιχεία που τα παρέχει το σχολείο" },

            { TextMapper.GUIR_OPT_SELECT_GROUP, "Επιλογή" },
            { TextMapper.GUIR_OPT_STUDENT, "Μαθητής" },
            { TextMapper.GUIR_OPT_TEACHER, "Εκπαιδευτικός" },
            { TextMapper.GUIR_OPT_ADMIN, "Διαχειριστής"},
            { TextMapper.GUIR_OPT_SCHOOLADMIN, "Διαχειριστής Σχολείου" },
            { TextMapper.GUIR_OPT_SCHOOLCODE, "Κλειδί"},
            
            { TextMapper.GUIR_ERR_REGISTER, "Προέκυψε σφάλμα" },

            { TextMapper.GUIR_MSG_REGISTERED, "Επιτυχής εγγραφή"},

            { TextMapper.GUIM_DWO_FULL, "Digital Mathematics Environment" },
            { TextMapper.GUIM_DWO_SHORT, "DME" },
            { TextMapper.GUIM_FI_NAME, "Freudenthal Institute"},
            { TextMapper.GUIM_MAIN_MENU, "Modules" },

            { TextMapper.GUIL_LOGGED_IN_AS, "Σύνδεση ως" },
            { TextMapper.GUIL_NOT_LOGGED_IN, "Δεν έχετε συνδεθεί"},
            { TextMapper.GUIL_BTN_LOGIN, "Σύνδεση"},
            { TextMapper.GUIL_BTN_LOGOFF, "Αποσύνδεση" },

            { TextMapper.GUIMNU_MAIN_MENU, "Modules" },
            { TextMapper.GUIMNU_MY_PROFILE, "Το προφίλ μου" },
            { TextMapper.GUIMNU_STUDENT_IN_CLASS, "Μαθητής της τάξης" },
            { TextMapper.GUIMNU_STUDENT_NO_CLASS_0, "Δεν είστε ακόμα "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_1, "μέλος της τάξης.  "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_2, "Μετάβαση στο"},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_3, "\"Το προφίλ μου\" και "},
         	{ TextMapper.GUIMNU_STUDENT_NO_CLASS_4, "επιλογή τάξης."},
            { TextMapper.GUIMNU_CLASS_RESULTS, "Αποτελέσματα τάξης" },
            { TextMapper.GUIMNU_RESULTS, "Δείτε τα αποτελέσματα" },
            { TextMapper.GUIMNU_CLASS_MANAGEMENT, "Διαχείριση τάξης" },
            { TextMapper.GUIMNU_SCHOOL_MANAGEMENT, "Διαχείριση σχολείου" },
            { TextMapper.GUIMNU_COURSE_MANAGEMENT, "Διαχείριση Module"},
            { TextMapper.GUIMNU_MSG_ADD_CLASS, "Όνομα της νέας τάξης" },
            { TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE, "Προσθήκη νέας τάξης" },
            { TextMapper.GUIMNU_MSG_ADD_SCHOOL, "Όνομα του νέου σχολείου"},   
         	{ TextMapper.GUIMNU_MSG_ADD_SCHOOL_TITLE, "Προσθήκη νέου σχολείου"},
         	{ TextMapper.GUIMNU_USERS_SCHOOL, "Χρήστες σχολείου"},
            { TextMapper.GUIMNU_CLASSES_SCHOOL, "Τάξεις σχολείου"},
            { TextMapper.GUIMNU_FEATURES_SCHOOLADMIN, "Χαρακτηριστικά διαχειριστή σχολείου"},
            
            { TextMapper.GUIUMP_MANAGE_USERS, "Διαχείριση Χρηστών"},
            { TextMapper.GUIUMP_REMOVE_FROM_SCHOOL, "Αφαίρεση μόνο από το σχολείο"},
            { TextMapper.GUIUMP_REMOVE_COMPLETE, "Αφαίρεση πλήρους λογαριασμού"},
            { TextMapper.GUIUMP_ADD_STUDENTS, "Προσθήκη νέων μαθητών"},
            { TextMapper.GUIUMP_ADD_TEACHERS, "Προσθήκη νέων εκπαιδευτικών"},
            { TextMapper.GUIUMP_IMPORT_CLIPBOARD, "Εισαγωγή από το clipboard"},
            { TextMapper.GUIUMP_MAKE_ACCOUNTS, "Δημιουργία λογαριασμών"},
            { TextMapper.GUIUMP_EXTRA_ROW, "Πρόσθετη σειρά"},

         	{ TextMapper.GUICO_HEADER, "Modules"},
         	{ TextMapper.GUICO_SCO_LIST_TITLE, "Δραστηριότητες"},

            { TextMapper.GUIP_MY_PROFILE, "Το προφίλ μου" },
            { TextMapper.GUIP_REGISTERINFO, "Πληροφορίες εγγραφής" },
            { TextMapper.GUIP_PERSONALINFO, "Προσωπικά στοιχεία" },
            { TextMapper.GUIP_SCHOOLINFO, "Στοιχεία σχολείου" },

            { TextMapper.GUIP_USERNAME, "Όνομα χρήστη" },
            { TextMapper.GUIP_OLD_PASSWORD, "Τρέχων κωδικός" },
            { TextMapper.GUIP_PASSWORD, "Νέος κωδικός" },
            { TextMapper.GUIP_RE_PASSWORD, "Επαλήθευση κωδικού" },

            { TextMapper.GUIP_FIRSTNAME, "Όνομα" },
            { TextMapper.GUIP_MIDDLENAME, "Δεύτερο Όνομα" },
            { TextMapper.GUIP_LASTNAME, "Επώνυμο" },
            { TextMapper.GUIP_EMAIL, "Διεύθυνση E-mail" },

            { TextMapper.GUIP_SCHOOLLOGIN, "Σύνδεση σχολείου" },
            { TextMapper.GUIP_SCHOOLGROUP, "Είμαι" },
            { TextMapper.GUIP_SCHOOLPASSWORD, "Κωδικός" },
            { TextMapper.GUIP_CLASS, "Τάξη" },

            { TextMapper.GUIP_BTN_SAVE, "Αποθήκευση" },
            { TextMapper.GUIP_BTN_RESET, "Επαναφορά" },
            { TextMapper.GUIP_BTN_DELETE_PROFILE, "Διαγραφή προφίλ" },

            { TextMapper.GUIP_MSG_PROVIDED_SCHOOL, "Στοιχεία που παρέχει το σχολείο" },

            { TextMapper.GUIP_ERR_CHANGE, "Προέκυψε σφάλμα" },

            { TextMapper.GUIP_OPT_SELECT_GROUP, "Επιλογή" },

            { TextMapper.GUIP_CONFIRM_REMOVE_USER, "Είστε σίγουρος/η ότι θέλετε να διαγράψετε το λογαριασμό σας" },
            { TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE, "Διαγραφή λογαριασμού" },

            { TextMapper.GUIP_MSG_PROFILE_CHANGED, "Ο λογαριασμός σας άλλαξε επιτυχώς"},

            { TextMapper.GUIPT_SCHOOL, "Σχολείο" },
            { TextMapper.GUIPT_TEACHER_FROM_CLASS, "Εκπαιδευτικός της τάξης" },
            { TextMapper.GUIPT_BTN_ADD_CLASS, "Προσθήκη τάξης" },
            
         { TextMapper.GUIS_STUDENTS, "Μαθητές"},
         { TextMapper.GUIS_TEACHERS, "Εκπαιδευτικοί"},
         { TextMapper.GUIS_SCHOOL_MANAGEMENT, "Διαχείριση σχολείου"},

         { TextMapper.GUIS_TLTP_DELETE_SCHOOL, "Διαγραφή σχολείου {0} "},
         { TextMapper.GUIS_TLTP_EDIT_SCHOOL, "Επεξεργασία ονόματος σχολείου"},
         { TextMapper.GUIS_TLTP_USERS_SCHOOL, "Μαθητές του {0}"},
         
         { TextMapper.GUIS_ADD_SCHOOL, "Προσθήκη σχολείου"},
         { TextMapper.GUIS_DELETE_SCHOOL, "Διαγραφή σχολείου"},
         { TextMapper.GUIS_RENAME_SCHOOL, "Επεξεργασία ονόματος σχολείου"}, 
         { TextMapper.GUIS_MSG_RENAME_SCHOOL, "Εισαγωγή νέου ονόματος σχολείου"},
         { TextMapper.GUIS_MSG_DELETE_SCHOOL, "Είστε σίγουρος/η ότι θέλετε να διαγράψετε αυτό το σχολείο"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY, "Αυτό το σχολείο περιλαμβάνει χρήστες. Είστε σίγουρος/η ότι θέλετε να διαγράψετε αυτό το σχολείο"},
         { TextMapper.GUIS_SCHOOL_NOT_EMPTY_TITLE, "Αυτό το σχολείο περιλαμβάνει χρήστες."},
         { TextMapper.GUIS_MSG_DELETE_STUDENT, "Είστε σίγουρος ότι θέλετε να διαγράψετε το {0} από αυτό το σχολείο"},
         { TextMapper.GUIS_DELETE_STUDENT, "Διαγραφή μαθητών από αυτό το σχολείο"},
         { TextMapper.GUIS_NO_STUDENTS, "Το σχολείο {0} δεν περιλαμβάνει μαθητές"}, 

         { TextMapper.GUIC_STUDENTS, "Μαθητές"},
         { TextMapper.GUIC_CLASS_MANAGEMENT, "Διαχείριση τάξης"},

         { TextMapper.GUIC_TLTP_DELETE_CLASS, "Διαγραφή τάξης {0} "},
         { TextMapper.GUIC_TLTP_EDIT_CLASS, "Επεξεργασία ονόματος τάξης"},
         { TextMapper.GUIC_TLTP_USERS_CLASS, "Μαθητές στην τάξη {0}"},

         ////peter
         { TextMapper.GUIC_TLTP_ASSIGN_CLASS, "Ανάθεση module σε τάξη {0}"},
		 ////peter

            { TextMapper.GUIC_STUDENTS, "Μαθητές" },
            { TextMapper.GUIC_ADD_CLASS, "Δημιουργία τάξης"},
            { TextMapper.GUIC_DELETE_CLASS, "Διαγραφή τάξης" },
            { TextMapper.GUIC_RENAME_CLASS, "Επεξεργασία ονόματος τάξης" },
            { TextMapper.GUIC_MSG_RENAME_CLASS, "Νέο όνομα τάξης" },
            { TextMapper.GUIC_MSG_DELETE_CLASS, "Είστε σίγουροι ότι θέλετε να διαγράψετε την τάξη" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY, "Υπάρχουν κάποιοι μαθητές στην τάξη. Είστε σίγουροι ότι θέλετε να διαγράψετε την τάξη" },
            { TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE, "Υπάρχουν κάποιοι μαθητές στην τάξη" },
         { TextMapper.GUIC_MSG_DELETE_STUDENT, "Είστε σίγουροι ότι θέλετε να διαγράψετε το {0} από την τάξη"},
         { TextMapper.GUIC_DELETE_STUDENT, "Διαγραφή μαθητή από τάξη"},
         { TextMapper.GUIC_NO_STUDENTS, "Δεν υπάρχουν μαθητές στην τάξη {0}"},

         { TextMapper.GUIRS_RESULTS, "Αποτελέσματα"},
         { TextMapper.GUIRS_NO_RESULTS, "Δεν υπάρχουν αποτελέσματα"},
         { TextMapper.GUIRS_BTN_SELECT_COURSES, "Επιλογή Modules"},

         { TextMapper.GUIRS_TLTP_SELECT_COURSES, "Επιλέξτε ένα module"},

         { TextMapper.GUIRS_TLTP_ZOOM, "Αποτελέσματα από {0}"},
         { TextMapper.GUIRS_TLTP_ZOOM_ORDER, "Ταξινόμηση κατά {0}"},

         { TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON, "Εμφάνιση αποτελεσμάτων της Δραστηριότητας {0} από {1}"},
         { TextMapper.GUIRSDLG_MSG, "Διαγραφή όλων των αποτελεσμάτων του ''{0}'' για {1}?"},

         { TextMapper.UG_RESULTS_OF_STUDENT, "Αποτελέσματα Δραστηριότητας {0} από {1}"},

         { TextMapper.GUISC_TITLE, "Επιλογή modules"},
         { TextMapper.GUISC_BTN_SELECT_ALL, "Επιλογή όλων"},
         { TextMapper.GUISC_BTN_DESELECT_ALL, "Αποεπιλογή όλων"},

         { TextMapper.UG_CLASSES, "Τάξεις"},
         { TextMapper.UG_STUDENTS_OF_CLASS, "Μαθητές της {0}"},

         { TextMapper.UG_USER_TITLE,"Μαθητής"},
         { TextMapper.UG_CLASS_TITLE,"Τάξη"},

         { TextMapper.UG_CLASS_CHILD, "Μαθητές {0}"},
         { TextMapper.UG_CLASS_ORDER_ASC, "Όνομα Τάξης (Α-Ω)"},
         { TextMapper.UG_CLASS_ORDER_DESC, "Όνομα Τάξης (Ω-Α)"},

         { TextMapper.UG_USER_PARENT, "Τάξεις"},
         { TextMapper.UG_USER_ORDER_ASC, "επώνυμο (Α-Ω)"},
         { TextMapper.UG_USER_ORDER_DESC, "επώνυμο (Ω-Α)"},

         { TextMapper.LG_COURSES, "Modules"},
         { TextMapper.LG_SCOS_OF_COURSE, "Δραστηριότητες του {0}"},

         { TextMapper.LG_COURSE_CHILD, "Δραστηριότητες του {0}"},
         { TextMapper.LG_COURSE_ORDER_ASC, "αποτελέσματα (0-100)"},
         { TextMapper.LG_COURSE_ORDER_DESC, "αποτελέσματα (100-0)"},

         { TextMapper.LG_SCO_PARENT, "modules"},
         { TextMapper.LG_SCO_ORDER_ASC, "αποτελέσματα (0-100)"},
         { TextMapper.LG_SCO_ORDER_DESC, "αποτελέσματα (100-0)"},

         { TextMapper.LG_SCO_NAME , "Δραστηριότητες {0}"},

         { TextMapper.GUIC_ADD_COURSE, "Προσθήκη νέου module"},
         { TextMapper.GUIC_ADD_MAP, "Προσθήκη νέου φακέλου" },
         { TextMapper.GUIC_COURSE_MANAGEMENT, "Διαχείριση Module"},

         { TextMapper.GUIC_TLTP_DELETE_COURSE, "Διαγραφή module {0}"},
         { TextMapper.GUIC_TLTP_DELETE_MAP, "Διαγραφή φακέλου {0}"},
         { TextMapper.GUIC_TLTP_EDIT_COURSE, "Επεξεργασία module"},
         { TextMapper.GUIC_TLTP_SCO_COURSE, "Διαχείριση Δραστηριοτήτων"},

         { TextMapper.GUICDLG_COURSE_NAME, "Όνομα Module"},
         { TextMapper.GUICDLG_MAP_NAME, "Ονομα φακέλου" },

         { TextMapper.GUICDLG_COURSE_DESCRIPTION, "Περιγραφή"},

         { TextMapper.GUICDLG_TTL_ADD_COURSE, "Προσθήκη νέου module"},
         { TextMapper.GUICDLG_TTL_EDIT_COURSE, "Επεξεργασία module"},
         { TextMapper.GUIC_TLTP_EDIT_MAP, "Επεξεργασία φακέλου" },

         { TextMapper.GUIC_NO_COURSES, "Δεν υπάρχουν modules για να εμφανιστούν"},
         { TextMapper.GUIC_COURSE_SHARE, "Διαμοιρασμός modules" },

         { TextMapper.GUIC_MSG_COURSE_DELETE, "Δεν υπάρχουν Δραστηριότητες. \nWhen you delete the module \nen the results of the Activities will also be deleted.\n \nAre you sure you want to delete the module;"},
         { TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO, "Είστε σίγουροι ότι θέλετε να διαγράψετε το module;"},
         { TextMapper.GUIC_MSG_TTL_COURSE_DELETE, "Διαγραφή module"},

         { TextMapper.GUIS_ADD_SCO, "Προσθήκη νέας Δραστηριότητας"},
         { TextMapper.GUIS_LBL_SCO_OF_COURSE, "Δραστηριότητες του {0}"},
         { TextMapper.GUIS_SCO_MANAGEMENT, "Διαχείριση Δραστηριοτήτων"},
         { TextMapper.GUIS_SHOW_SCORE, "Οι μαθητές βλέπουν τα αποτελέσματά τους"},


         { TextMapper.GUIS_TLTP_DELETE_SCO, "Διαγραφή Δραστηριότητας {0}"},
         { TextMapper.GUIS_TLTP_EDIT_SCO, "Επεξεργασία ονόματος Δραστηριότητας"},
         { TextMapper.GUIS_TLTP_PARAMETERS_SCO, "Επεξεργασία Δραστηριότητας"},
         { TextMapper.GUIS_TLTP_COURSE_SCO, "Πίσω στα modules"},

         { TextMapper.GUISDLG_SCO_NAME, "Όνομα Δραστηριότητας"},
         { TextMapper.GUISDLG_SCO_DESCRIPTION, "Περιγραφή Δραστηριότητας"},

         { TextMapper.GUISDLG_TTL_ADD_SCO, "Προσθήκη νέας Δραστηριότητας"},
         { TextMapper.GUISDLG_TTL_EDIT_SCO, "Επεξεργασία Δραστηριότητας"},

         { TextMapper.GUIS_MSG_SCO_DELETE, "Όταν διαγράψετε τη Δραστηριότητα \nen θα διαγραφούν και τα αποτελέσματα.\n \nΕίστε σίγουροι ότι θέλετε να διαγράψετε τη Δραστηριότητα;"},
         { TextMapper.GUIS_MSG_TTL_SCO_DELETE, "Διαγραφή Δραστηριότητας"},
         { TextMapper.GUIS_NO_SCOS, "Δεν υπάρχουν Δραστηριότητες στο module {0}"},
         { TextMapper.GUIS_LOAD_LOGO, "Φόρτωση εικόνας του {0}"},
         
         { TextMapper.GUISDLG_BTN_ADD_SCO, "Προσθήκη"},
         { TextMapper.GUISDLG_BTN_PREVIEW_SCO, "Προεπισκόπηση Δραστηριότητας"},
         { TextMapper.GUISDLG_MSG_SELECT_SCO, "Επιλογή Δραστηριότητας"},
         { TextMapper.GUISDLG_MSG_NO_APPLETS, "Δεν υπάρχουν Δραστηριότητες για να προστεθούν"},
         { TextMapper.GUISDLG_SHOW, "Εμφάνιση"},
         { TextMapper.GUISDLG_ALL, "Όλα"},
         { TextMapper.GUISDLG_MSG_NO_SELECTION, "Δεν έχετε επιλέξει Δραστηριότητες"},
         { TextMapper.GUISDLG_RB_STANDARD_SCOS, "Υπάρχουσες Δραστηριότητες"},
         { TextMapper.GUISDLG_RB_OWN_SCOS, "Δικές σου Δραστηριότητες"},

         { TextMapper.GUIPA_BTN_PREVIEW, "Προεπισκόπηση Δραστηριότητας"},
         { TextMapper.GUIPA_BTN_SAVE, "Αποθήκευση"},
         { TextMapper.GUIPA_BTN_RESET, "Επαναφορά"},
         { TextMapper.GUIPA_BTN_CANCEL, "Κλείσιμο"},
         
         { TextMapper.GUIPA_SCO_EDIT, "Επεξεργασία Δραστηριότητας"},
         
         { TextMapper.GUIPA_NO_PARAMS, "Αυτή η Δραστηριότητα δεν μπορεί να τροποποιηθεί"},

         { TextMapper.GUIPA_DLG_TTL, "Δραστηριότητα {0} σε κατάσταση επεξεργασίας"},
         
         { TextMapper.GUIPA_MSG_PARAM_SAVE, "Αν αποθηκεύσετε αυτή τη νέα διαμόρφωση,\nτο αποτέλεσμα των προηγούμενων αντικειμένων θα διαγραφεί\n \nΕίστε σίγουροι ότι θέλετε να αποθηκεύσετε αυτή τη διαμόρφωση?"},
         { TextMapper.GUIPA_MSG_TTL_PARAM_SAVE, "Αποθήκευση διαμόρφωσης"},
         
         { TextMapper.GUIPA_PARAMS_OF_SCO, "Παράμετροι ({0})"},
         
         { "cut", "Αποκοπή" },
         { "copy" , "Αντιγραφή" },
         { "paste", "Επικόλληση"},
         { "delete", "Διαγραφή" },
         { "edit", "Επεξεργασία" },
         { "file", "Αρχείο" },
         { "rename", "Μετονομασία" },

         { TextMapper.GUIA_INSERT_SCOS, "Εισαγωγή δραστηριοτήτων από backup"},
         { TextMapper.GUIH_STOP_EDIT, "Τέλος Επεξεργασίας" },
         { TextMapper.GUIH_EDIT, "Επεξεργασία" },
         
         { "Alle modules", "Όλα τα modules"},
         { "Standaard DWO modules", "Υπάρχοντα DME modules"},

         { "Nieuwe Modulemap", "Νέος φάκελος για Module" },

         // classadminpanel
         { "Klassen toewijzen", "Ανάθεση τάξης" },
         { "Klas", "Τάξη" },
         { "Docent", "Εκπαιδευτικός" },
         { "Verwijder", "Αφαίρεση" },
         // classpanel 
         { "boomstructuur?", "δενδροειδής απεικόνιση;" },
         // select courses dialog
         { "Leerlinggegevens verwijderen", "Αφαίρεση αποτελεσμάτων μαθητών" },
         { "Wilt u alle resultaten van {0} voor {1} verwijderen?", "Θέλετε να αφαιρέσετε όλα τα αποτελέσματα του {0} για {1}?" },
         { "soort", "είδος" },
         { "vanaf", "από" },
         { "tot aan", "μέχρι" },
         { "tot", "μέχρι" },
         { "Ll ggvns", "αποτελέσματα" },
         { "normaal", "κανονικό" },
         { "afgeschermd", "ασφαλές" },
         { "Geef tijdstip {0}", "Ορισμός ημερομηνίας και ώρας \"{0}\""},
         { " dag: " , " ημερομηνία: " },
         { "tijd:", "ώρα:" },
         // resultLoogger 
         { "Overzicht Logs", "Προεπισκόπηση Logs" },
         { "deel-scores", "μερική βαθμολογία" },
         { "tijdsduur", "χρονική διάρκεια" },
         // default partial score
         { "resultaat", "αποτέλεσμα" },
         //importexportdialog
         {"Kopiëer modules", "Αντιγραφή modules" }, 
         {"Toestaan", "Επιτρέπεται" },
         {"Modules beschikbaar stellen", "Παροχή modules" },
         {"Modules opvragen", "Αναζήτηση modules"},
         {"Delen met","Διαμοιραμός με"},
         {"Alle scholen","Όλα τα σχολεία"},
         {"Scholen", "Σχολεία"},
         {"toepassen", "Εφαρμογή"},
         
         { TextMapper.GUIEID_MSG1, "<html>(1) Επιλέξτε σχολείου<br>" +
			   "(2) Eventually preview the shown modules<br>" +
			   "(3) Επιλέξτε ένα ή περισσότερα modules για χρήση στο σχολείο σας<br><br>" +
			   "Τα επιλεγμένα modules έχουν αντιγραφεί στο δικό σου module view<br>"+
			   "και μπορούν να χρησιμοποιηθούν από το σχολείο σου." },
		  { TextMapper.GUIEID_MSG2, "<html>Επιθυμώ να συμμετέχω σε αυτόν τον τρόπο διαμοιρασμού και να είμαι ορατός ως σχολείο στις λίστες"},
		  { TextMapper.GUIEID_MSG3, "<html>(1) Επιλογή modules<br>(2) Επιλογή  σχολείων<br><br>Τα επιλεγμένα modules είναι διαθέσιμα<br>για τα επιλεγμένα σχολεία." },

};

 public Text_gr() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}