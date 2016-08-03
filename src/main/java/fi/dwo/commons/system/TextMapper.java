// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\TextMapper.java
package fi.dwo.commons.system;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class TextMapper {

	public static final String DEFAULT_LANGUAGE = "nl";

	private static final String TEXT_CLASS = "Text";

	public final static String USER_GUEST = "USER_GUEST";

	/* General word constants */
	public static final String LBL_USERNAME = "LBL_USERNAME";
	public static final String LBL_PASSWORD = "LBL_PASSWORD";
        public static final String LBL_EMAIL = "LBL_EMAIL";
        public static final String LBL_REQUEST_NEW_PASSWORD = "LBL_REQUEST_NEW_PASSWORD";
        public static final String LBL_ENTER_AUTHCODE_FOR_NEW_PASSWORD = "LBL_ENTER_AUTHCODE_FOR_NEW_PASSWORD";
        public static final String LBL_ILLEGAL_AUTHCODE = "LBL_ILLEGAL_AUTHCODE";
        public static final String LBL_UNKNOWN_COMBINATION = "LBL_UNKNOWN_COMBINATION";
        public static final String LBL_EMAIL_WITH_AUTHCODE_SENT="LBL_EMAIL_WITH_AUTHCODE_SENT";

	/* Button text constants */
	public static final String BTN_LOGIN = "BTN_LOGIN";

	public static final String BTN_CANCEL = "BTN_CANCEL";

	public static final String BTN_OK = "BTN_OK";

	public static final String BTN_YES = "BTN_YES";
	public static final String BTN_NO = "BTN_NO";

	public static final String BTN_CLOSE = "BTN_CLOSE";

	public static final String BTN_ADD = "BTN_ADD";
 
	public static final String BTN_BACK = "BTN_BACK";
	public static final String BTN_NEW_STUDENTS = "BTN_ADD_STUDENTS";
	public static final String BTN_NEW_TEACHERS = "BTN_ADD_TEACHERS";
	public static final String BTN_CREATE_STUDENTACCOUNTS = "BTN_CREATE_STUDENTACCOUNTS";
	public static final String BTN_NEW_CLASS = "BTN_ADD_CLASS";
	public static final String BTN_DELETE = "BTN_DELETE";
	public static final String BTN_COPYSELECTEDTOCLASS = "BTN_COPYTOSCHOOLCLASS";
	public static final String BTN_DELSELECTED = "BTN_DELETE";
	public static final String BTN_UPDATE = "BTN_UPDATE";
    public static final String DLG_COPYSTUDENTERROR = "DLG_COPYSTUDENTERROR";
    public static final String DLG_NO_USERS_SELECTED = "DLG_NO_USERS_SELECTED";

	/* labels for radio buttons and others */
	public static final String LBL_STUDENTS = "LBL_STUDENTS";
	public static final String LBL_TEACHERS = "LBL_TEACHERS";
	public static final String LBL_SCHOOLADMINS = "LBL_SCHOOLADMINS";

	/* header panel headers */
	public static final String HDR_NEW_STUDENTS = "HDR_NEW_STUDENTS";
	public static final String HDR_EDITSTUDENTS = "HDR_EDITSTUDENTS";
	public static final String HDR_EDITTEACHERS = "HDR_EDITTEACHERS";
	public static final String HDR_SCHOOLCLASS = "HDR_SCHOOLCLASS";

	/* Table headers */
	public static final String TBL_SCHOOL = "TBL_SCHOOL";
	public static final String TBL_LOGIN = "TBL_LOGIN";
	public static final String TBL_ROLE = "TBL_ROLE";
	public static final String TBL_DELETE = "TBL_DELETE";
	public static final String TBL_EDIT = "TBL_EDIT";
	public static final String TBL_SELECT = "TBL_SELECT";

	public static final String TBL_GIVENNAME = "TBL_GIVENNAME";
	public static final String TBL_INSERTION = "TBL_INSERTION";
	public static final String TBL_FAMILYNAME = "TBL_FAMILYNAME";
	public static final String TBL_USERNAME = "TBL_USERNAME";
	public static final String TBL_PASSWORD = "TBL_PASSWORD";
	public static final String TBL_EMAIL = "TBL_EMAIL";
	public static final String TBL_EDITCLASS = "TBL_EDITCLASS";
	public static final String TBL_EDITMODULES = "TBL_EDITMODULES";
	public static final String TBL_EDITSTUDENTS = "TBL_EDITSTUDENTS";
	public static final String TBL_EDITTEACHERS = "TBL_EDITTEACHERS";
	public static final String TBL_CLASSNAME = "TBL_CLASSNAME";
	public static final String TBL_CLASSLIST = "TBL_CLASSLIST";
	public static final String TBL_SCHOOLNAME = "TBL_SCHOOLNAME";
	public static final String TBL_SCHOOLLOGIN = "TBL_SCHOOLLOGIN";
	public static final String TBL_SCHOOLRIGHTS = "TBL_SCHOOLRIGHTS";

	/* Message dialog messages */
	public static final String DLG_CONFIRM = "DLG_CONFIRM";
	public static final String DLG_Q_REMOVE = "DLG_Q_REMOVE";
	public static final String DLG_DONE_MSG = "DLG_DONE_MSG";
	public static final String DLG_NO_STUDENTS_SELECTED = "DLG_NO_STUDENTS_SELECTED";
	public static final String DLG_NO_TEACHERS_SELECTED = "DLG_NO_TEACHERS_SELECTED";

	public static final String DLG_MESSAGE = "DLG_MESSAGE";
	public static final String DLG_ERROR = "DLG_ERROR";
        public static final String DLG_SERVER_OUT = "DLG_SERVER_OUT";
	public static final String DLG_CREATESTUDENTERROR = "DLG_CREATESTUDENTERROR";
	public static final String DLG_CREATETEACHERERROR = "DLG_CREATETEACHERERROR";
        public static final String DLG_Q_LOSE_NEW_STUDENT_ACCOUNTS = "DLG_Q_LOSE_NEW_STUDENT_ACCOUNTS";
        public static final String DLG_Q_LOSE_NEW_TEACHER_ACCOUNTS = "DLG_Q_LOSE_NEW_TEACHER_ACCOUNTS";
        public static final String DLG_Q_REMOVE_SCHOOLCLASS_BY_NAME = "DLG_Q_REMOVE_SCHOOLCLASS_BY_NAME";
        public static final String DLG_Q_REMOVE_TEACHER_BY_NAME = "DLG_Q_REMOVE_TEACHER_BY_NAME";
        
	public static final String DLG_ENTER_INPUT = "DLG_ENTER_INPUT";
	/* Exception Messages */
	public final static String EX_UNKNOWN_ERROR = "EX_UNKNOWN_ERROR";

	/* Register Exception Messages */
	public final static String EXR_USER_EXISTS = "EXR_USER_EXISTS";
	public final static String EXR_USER_EXISTS2 = "EXR_USER_EXISTS {0}";
    /* Welcome Panel */
    public final static String GUIW_LOGINDATA = "GUIW_LOGINDATA";
    public final static String GUIW_USERNAME = "GUIW_USERNAME";
    public final static String GUIW_PASSWORD = "GUIW_PASSWORD";
    public final static String GUIW_WELCOME = "GUIW_WELCOME";
    public final static String GUIW_REGISTER = "GUIW_REGISTER";

	public final static String EXR_WRONG_USERNAME_PASSWORD = "EXR_WRONG_USERNAME_PASSWORD";

	public final static String EXR_WRONG_SECOND_PASSWORD = "EXR_WRONG_SECOND_PASSWORD";

	public final static String EXR_UNKNOWN_SCHOOLGROUP = "EXR_UNKNOWN_SCHOOLGROUP";

	public final static String EXR_WRONG_FORMAT = "EXR_WRONG_FORMAT";
	public final static String EXR_WRONG_EMAILFORMAT = "EXR_WRONG_EMAILFORMAT";

	/* School Exception Messages */
	public final static String EXS_SCHOOL_EXISTS = "EXS_SCHOOL_EXISTS";

	/* SchoolClass Exception Messages */
	public final static String EXC_CLASS_EXISTS = "EXC_CLASS_EXISTS";
	public final static String EXR_MANDATORY = "EXR_MANDATORY";

	/* Course Exception Messages */
	public final static String EXC_COURSE_EXISTS = "EXC_COURSE_EXISTS";

	/* Sco Exception Messages */
	public final static String EXS_SCO_EXISTS = "EXS_SCO_EXISTS";
	public final static String EXS_NO_APPLET = "EXS_NO_APPLET";

	/* Login Exception Messages */
	public final static String EXL_UNKNOWN_USER = "EXL_UNKNOWN_USER";

	/* Gui Messages */
	public final static String GUI_WAIT_A_MOMENT = "GUI_WAIT_A_MOMENT";

	/* Reauthenticate Panel */
	public final static String GUIREAUTH_AREYOUSURE = "GUIREAUTH_AREYOUSURE";

	/* Welcome Panel */

	public final static String GUIW_GUESTLOGIN = "GUIW_GUESTLOGIN";
	public final static String GUIW_MSG_WORK_NOT_SAVE = "GUIW_MSG_WORK_NOT_SAVE";
	public final static String GUIW_MSG_REGISTER_NEW = "GUIW_MSG_REGISTER_NEW";
	public final static String GUIW_MSG_REGISTER_EXISTING = "GUIW_MSG_REGISTER_EXISTING";

	public final static String GUIW_BTN_LOGIN = "GUIW_BTN_LOGIN";
	public final static String GUIW_BTN_GUESTLOGIN = "GUIW_BTN_GUESTLOGIN";
	public final static String GUIW_BTN_REGISTER = "GUIW_BTN_REGISTER";

	public final static String GUIW_ERR_LOGIN = "GUIW_ERR_LOGIN";
	public final static String GUIW_ERR_NEW_SCHOOLLOGIN = "GUIW_ERR_NEW_SCHOOLLOGIN";
	public final static String GUIW_ERR_NOROLE = "GUIW_ERR_NOROLE";

	/* Register Panel */
	public final static String GUIR_REGISTER = "GUIR_REGISTER";

	public final static String GUIR_REGISTERINFO = "GUIR_REGISTERINFO";
	public final static String GUIR_PERSONALINFO = "GUIR_PERSONALINFO";
	public final static String GUIR_SCHOOLINFO = "GUIR_SCHOOLINFO";

	public final static String GUIR_USERNAME = "GUIR_USERNAME";
	public final static String GUIR_PASSWORD = "GUIR_PASSWORD";
	public final static String GUIR_RE_PASSWORD = "GUIR_RE_PASSWORD";

	public final static String GUIR_FIRSTNAME = "GUIR_FIRSTNAME";
	public final static String GUIR_MIDDLENAME = "GUIR_MIDDLENAME";
	public final static String GUIR_LASTNAME = "GUIR_LASTNAME";
	public final static String GUIR_EMAIL = "GUIR_EMAIL";

	public final static String GUIR_SCHOOLLOGIN = "GUIR_SCHOOLLOGIN";
	public final static String GUIR_SCHOOLGROUP = "GUIR_SCHOOLGROUP";
	public final static String GUIR_SCHOOLPASSWORD = "GUIR_SCHOOLPASSWORD";

	public final static String GUIR_BTN_REGISTER = "GUIR_BTN_REGISTER";
	public final static String GUIR_BTN_RESET = "GUIR_BTN_RESET";
	public final static String GUIR_BTN_BACK = "GUIR_BTN_BACK";

	public final static String GUIR_MSG_PROVIDED_SCHOOL = "GUIR_MSG_PROVIDED_SCHOOL";
    public final static String GUIP_REGISTERINFO = "GUIP_REGISTERINFO";
    public final static String GUIP_PERSONALINFO = "GUIP_PERSONALINFO";
    public final static String GUIP_SCHOOLINFO = "GUIP_SCHOOLINFO";
    public final static String GUIP_ACCOUNTANDCONTACTINFO = "GUIP_ACCOUNTANDCONTACTINFO";
    public final static String GUIP_USERNAME = "GUIP_USERNAME";
    public final static String GUIP_OLD_PASSWORD = "GUIP_OLD_PASSWORD";
    public final static String GUIP_PASSWORD = "GUIP_PASSWORD";
    public final static String GUIP_RE_PASSWORD = "GUIP_RE_PASSWORD";

	public final static String GUIR_ERR_REGISTER = "GUIR_ERR_REGISTER";

	public final static String GUIR_OPT_SELECT_GROUP = "GUIR_OPT_SELECT_GROUP";
	public final static String GUIR_OPT_STUDENT = "STUDENT";
	public final static String GUIR_OPT_TEACHER = "TEACHER";
	public final static String GUIR_OPT_ADMIN = "ADMIN";
	public final static String GUIR_OPT_SCHOOLCODE = "SCHOOLCODE";
	public final static String GUIR_OPT_NULLSCHOOL = "NULLSCHOOL";
	public final static String GUIR_OPT_SCHOOLADMIN = "SCHOOLADMIN";

	public final static String GUIR_MSG_REGISTERED = "GUIR_MSG_REGISTERED";

	/* Main Panel */
	public final static String GUIM_DWO_FULL = "GUIM_DWO_FULL";
	public final static String GUIM_DWO_SHORT = "GUIM_DWO_SHORT";
	public final static String GUIM_FI_NAME = "GUIM_FI_NAME";
	public final static String GUIM_MAIN_MENU = "GUIM_MAIN_MENU";

	/* Logged In Panel */
	public final static String GUIL_LOGGED_IN_AS = "GUIL_LOGGED_IN_AS";
	public final static String GUIL_NOT_LOGGED_IN = "GUIL_NOT_LOGGED_IN";
	public final static String GUIL_BTN_LOGIN = "GUIL_BTN_LOGIN";
	public final static String GUIL_BTN_LOGOFF = "GUIL_BTN_LOGOFF";

	/* Menu Panel */
	public final static String GUIMNU_MAIN_MENU = "GUIMNU_MAIN_MENU";
	public final static String GUIMNU_MY_PROFILE = "GUIMNU_MY_PROFILE";
	public final static String GUIMNU_STUDENT_IN_CLASS = "GUIMNU_STUDENT_IN_CLASS";
	public final static String GUIMNU_STUDENT_NO_CLASS_0 = "GUIMNU_STUDENT_NO_CLASS_0";
	public final static String GUIMNU_STUDENT_NO_CLASS_1 = "GUIMNU_STUDENT_NO_CLASS_1";
	public final static String GUIMNU_STUDENT_NO_CLASS_2 = "GUIMNU_STUDENT_NO_CLASS_2";
	public final static String GUIMNU_STUDENT_NO_CLASS_3 = "GUIMNU_STUDENT_NO_CLASS_3";
	public final static String GUIMNU_STUDENT_NO_CLASS_4 = "GUIMNU_STUDENT_NO_CLASS_4";

	public final static String GUIMNU_CLASS_RESULTS = "GUIMNU_CLASS_RESULTS";
	public final static String GUIMNU_RESULTS = "GUIMNU_RESULTS";
	public final static String GUIMNU_CLASS_MANAGEMENT = "GUIMNU_CLASS_MANAGEMENT";
	public final static String GUIMNU_SCHOOL_MANAGEMENT = "GUIMNU_SCHOOL_MANAGEMENT";
	public final static String GUIMNU_COURSE_MANAGEMENT = "GUIMNU_COURSE_MANAGEMENT";
	public final static String GUIMNU_MSG_ADD_CLASS = "GUIMNU_MSG_ADD_CLASS";
	public final static String GUIMNU_MSG_ADD_CLASS_TITLE = "GUIMNU_MSG_ADD_CLASS_TITLE";
	public final static String GUIMNU_MSG_ADD_SCHOOL = "GUIMNU_MSG_ADD_SCHOOL";
	public final static String GUIMNU_MSG_ADD_SCHOOL_TITLE = "GUIMNU_MSG_ADD_SCHOOL_TITLE";
    public final static String GUIP_CONFIRM_REMOVE_USER = "GUIP_CONFIRM_REMOVE_USER";
    public final static String GUIP_CONFIRM_REMOVE_USER_TITLE = "GUIP_CONFIRM_REMOVE_USER_TITLE";
    public final static String GUIP_MSG_USER_REMOVED = "GUIP_MSG_USER_REMOVED";
    public final static String GUIP_MSG_PROFILE_CHANGED = "GUIP_MSG_PROFILE_CHANGED";

    public final static String GUIP_ROLE_OPTIONS = "GUIP_ROLE_OPTIONS";
    public final static String GUIP_BTN_ADD_ROLE = "GUIP_BTN_ADD_ROLE";

	public final static String GUIMNU_USERS_SCHOOL = "GUIMNU_USERS_SCHOOL";
	public final static String GUIMNU_CLASSES_SCHOOL = "GUIMNU_CLASSES_SCHOOL";
	public final static String GUIMNU_FEATURES_SCHOOLADMIN = "GUIMNU_FEATURES_SCHOOLADMIN";

	/* User Management Panel */
	public final static String GUIUMP_MANAGE_USERS = "GUIUMP_MANAGE_USERS";
	public final static String GUIUMP_REMOVE_FROM_SCHOOL = "GUIUMP_REMOVE_FROM_SCHOOL";
	public final static String GUIUMP_REMOVE_COMPLETE = "GUIUMP_REMOVE_COMPLETE";
	public final static String GUIUMP_ADD_STUDENTS = "GUIUMP_ADD_STUDENTS";
	public final static String GUIUMP_ADD_TEACHERS = "GUIUMP_ADD_TEACHERS";
	public final static String GUIUMP_IMPORT_CLIPBOARD = "GUIUMP_IMPORT_CLIPBOARD";
	public final static String GUIUMP_MAKE_ACCOUNTS = "GUIUMP_MAKE_ACCOUNTS";
	public final static String GUIUMP_EXTRA_ROW = "GUIUMP_EXTRA_ROW";

	/* Course Panel */
	public final static String GUICO_HEADER = "GUICO_HEADER";
	public final static String GUICO_SCO_LIST_TITLE = "GUICO_SCO_LIST_TITLE";

	/* Profile Panel */
	public final static String GUIP_MY_PROFILE = "GUIP_MY_PROFILE";

	public final static String GUIP_FIRSTNAME = "GUIP_FIRSTNAME";
	public final static String GUIP_MIDDLENAME = "GUIP_MIDDLENAME";
	public final static String GUIP_LASTNAME = "GUIP_LASTNAME";
	public final static String GUIP_EMAIL = "GUIP_EMAIL";

	public final static String GUIP_SCHOOLLOGIN = "GUIP_SCHOOLLOGIN";
	public final static String GUIP_SCHOOLGROUP = "GUIP_SCHOOLGROUP";
	public final static String GUIP_SCHOOLPASSWORD = "GUIP_SCHOOLPASSWORD";
	public final static String GUIP_CLASS = "GUIP_CLASS";

	public final static String GUIP_BTN_EDIT = "GUIP_BTN_EDIT";
	public final static String GUIP_BTN_SAVE = "GUIP_BTN_SAVE";
	public final static String GUIP_BTN_RESET = "GUIP_BTN_RESET";
	public final static String GUIP_BTN_SWITCH_PROFILE = "GUIP_BTN_SWITCH_PROFILE";
	public final static String GUIP_BTN_DELETE_PROFILE = "GUIP_BTN_DELETE_PROFILE";
	public final static String GUIP_BTN_DELETE_ACCOUNT = "GUIP_BTN_DELETE_ACCOUNT";

	public final static String GUIP_MSG_PROVIDED_SCHOOL = "GUIP_MSG_PROVIDED_SCHOOL";

	public final static String GUIP_ERR_CHANGE = "GUIP_ERR_CHANGE";

	public final static String GUIP_OPT_SELECT_GROUP = "GUIP_OPT_SELECT_GROUP";

	/* Teacher-Profile Panel */
	public final static String GUIPT_SCHOOL = "GUIPT_SCHOOL";
	public final static String GUIPT_TEACHER_FROM_CLASS = "GUIPT_TEACHER_FROM_CLASS";
	public final static String GUIPT_BTN_ADD_CLASS = "GUIPT_BTN_ADD_CLASS";

	/* SchoolPanel */
	public final static String GUIS_STUDENTS = "GUIS_STUDENTS";
	public final static String GUIS_TEACHERS = "GUIS_TEACHERS";
	public final static String GUIS_SCHOOL_MANAGEMENT = "GUIS_SCHOOL_MANAGEMENT";
	public final static String GUIS_ADD_SCHOOL = "GUIS_ADD_SCHOOL";

	public final static String GUIS_TLTP_DELETE_SCHOOL = "GUIS_TLTP_DELETE_SCHOOL";
	public final static String GUIS_TLTP_EDIT_SCHOOL = "GUIS_TLTP_EDIT_SCHOOL";
	public final static String GUIS_TLTP_USERS_SCHOOL = "GUIS_TLTP_USERS_SCHOOL";

	public final static String GUIS_DELETE_SCHOOL = "GUIS_DELETE_SCHOOL";
	public final static String GUIS_DELETE_SCHOOL_TITLE = "GUIS_DELETE_SCHOOL_TITLE";
	public final static String GUIS_RENAME_SCHOOL = "GUIS_RENAME_SCHOOL";
	public final static String GUIS_MSG_RENAME_SCHOOL = "GUIS_MSG_RENAME_SCHOOL";
	public final static String GUIS_MSG_DELETE_SCHOOL = "GUIS_MSG_DELETE_SCHOOL";
	public final static String GUIS_SCHOOL_NOT_EMPTY = "GUIS_SCHOOL_NOT_EMPTY";
	public final static String GUIS_SCHOOL_NOT_EMPTY_TITLE = "GUIS_SCHOOL_NOT_EMPTY_TITLE";
	public final static String GUIS_MSG_DELETE_STUDENT = "GUIS_MSG_DELETE_STUDENT";
	public final static String GUIS_DELETE_STUDENT = "GUIS_DELETE_STUDENT";
	public final static String GUIS_NO_STUDENTS = "GUIS_NO_STUDENTS";

	/* Class Panel */
	public final static String GUIC_STUDENTS = "GUIC_STUDENTS";
	public final static String GUIC_CLASS_MANAGEMENT = "GUIC_CLASS_MANAGEMENT";
	public final static String GUIC_REGISTER_FOR_CLASS = "GUIC_REGISTER_FOR_CLASS";
	public final static String GUIC_DEREGISTER_FOR_CLASS = "GUIC_DEREGISTER_FOR_CLASS";
	public final static String GUIC_ADD_CLASS = "GUIC_ADD_CLASS";
	public final static String GUIC_ADD_CLASSTEACHER = "GUIC_ADD_CLASSTEACHER";
	public final static String GUIC_BTN_SWITCH_CLASS = "GUIC_BTN_SWITCH_CLASS";

	public final static String GUIC_TBL_CLASSNAME = "GUIC_TBL_CLASSNAME";

	public final static String GUIC_TLTP_DELETE_CLASS = "GUIC_TLTP_DELETE_CLASS";
	public final static String GUIC_TLTP_EDIT_CLASS = "GUIC_TLTP_EDIT_CLASS";
	public final static String GUIC_TLTP_USERS_CLASS = "GUIC_TLTP_USERS_CLASS";
	public final static String GUIC_TLTP_ASSIGN_CLASS = "GUIC_TLTP_ASSIGN_CLASS";

	public final static String GUIC_DELETE_CLASS = "GUIC_DELETE_CLASS";
	public final static String GUIC_DELETE_CLASS_TITLE = "GUIC_DELETE_CLASS_TITLE";
	public final static String GUIC_RENAME_CLASS = "GUIC_RENAME_CLASS";
	public final static String GUIC_MSG_RENAME_CLASS = "GUIC_MSG_RENAME_CLASS";
	public final static String GUIC_MSG_CLASS_CONFIGURATION = "GUIC_MSG_CLASS_CONFIGURATION";
	public final static String GUIC_MSG_CLASS_NAME = "GUIC_MSG_CLASS_NAME";
	public final static String GUIC_MSG_CLASS_REGISTRATIONKEY = "GUIC_MSG_CLASS_REGISTRATIONKEY";
        public final static String GUIC_MSG_CLASS_REGISTRATIONKEYQ = "GUIC_MSG_CLASS_REGISTRATIONKEYQ";
	public final static String GUIC_MSG_CLASS_REGISTRATIONKEY_TOOLTIP = "GUIC_MSG_CLASS_REGISTRATIONKEY_TOOLTIP";
	public final static String GUIC_MSG_CLASS_TREESTRUCTURE = "GUIC_MSG_CLASS_TREESTRUCTURE";
	public final static String GUIC_MSG_DELETE_CLASS = "GUIC_MSG_DELETE_CLASS";
	public final static String GUIC_CLASS_NOT_EMPTY = "GUIC_CLASS_NOT_EMPTY";
	public final static String GUIC_CLASS_NOT_EMPTY_TITLE = "GUIC_CLASS_NOT_EMPTY_TITLE";
	public final static String GUIC_MSG_DELETE_STUDENT = "GUIC_MSG_DELETE_STUDENT";
	public final static String GUIC_DELETE_STUDENT = "GUIC_DELETE_STUDENT";
	public final static String GUIC_NO_STUDENTS = "GUIC_NO_STUDENTS";

	/* Results Panel */
	public final static String GUIRS_RESULTS = "GUIRS_RESULTS";
	public final static String GUIRS_NO_RESULTS = "GUIRS_NO_RESULTS";
	public final static String GUIRS_BTN_SELECT_COURSES = "GUIRS_BTN_SELECT_COURSES";
	public final static String GUIRS_BTN_COPY_TO_CLIPBOARD = "GUIRS_BTN_COPY_TO_CLIPBOARD";

	public final static String GUIRS_TLTP_SELECT_COURSES = "GUIRS_TLTP_SELECT_COURSES";

	public final static String GUIRS_TLTP_ZOOM = "GUIRS_TLTP_ZOOM";
	public final static String GUIRS_TLTP_ZOOM_ORDER = "GUIRS_TLTP_ZOOM_ORDER";

	public final static String GUIRS_TLTP_RESULT_SCORE_BUTTON = "GUIRS_TLTP_RESULT_SCORE_BUTTON";

	/* Select Courses Dialog */
	public final static String GUISC_TITLE = "GUISC_TITLE";
	public final static String GUISC_BTN_SELECT_ALL = "GUISC_BTN_SELECT_ALL";
	public final static String GUISC_BTN_DESELECT_ALL = "GUISC_BTN_DESELECT_ALL";

	/* UserGroup Strings */
	public final static String UG_CLASSES = "UG_CLASSES";
	public final static String UG_STUDENTS_OF_CLASS = "UG_STUDENTS_OF_CLASS";
	public final static String UG_RESULTS_OF_STUDENT = "UG_RESULTS_OF_STUDENT";

	public final static String UG_USER_TITLE = "UG_USER_TITLE";
	public final static String UG_CLASS_TITLE = "UG_CLASS_TITLE";

	public final static String UG_CLASS_CHILD = "UG_CLASS_CHILD";
	public final static String UG_CLASS_ORDER_ASC = "UG_CLASS_ORDER_ASC";
	public final static String UG_CLASS_ORDER_DESC = "UG_CLASS_ORDER_DESC";

	public final static String UG_USER_PARENT = "UG_USER_PARENT";
	public final static String UG_USER_ORDER_ASC = "UG_USER_ORDER_ASC";
	public final static String UG_USER_ORDER_DESC = "UG_USER_ORDER_DESC";

	/* LessonGroup Strings */
	public final static String LG_COURSES = "LG_COURSES";
	public final static String LG_SCOS_OF_COURSE = "LG_SCOS_OF_COURSE";

	public final static String LG_COURSE_CHILD = "LG_COURSE_CHILD";
	public final static String LG_COURSE_ORDER_ASC = "LG_COURSE_ORDER_ASC";
	public final static String LG_COURSE_ORDER_DESC = "LG_COURSE_ORDER_DESC";

	public final static String LG_SCO_PARENT = "LG_SCO_PARENT";
	public final static String LG_SCO_ORDER_ASC = "LG_SCO_ORDER_ASC";
	public final static String LG_SCO_ORDER_DESC = "LG_SCO_ORDER_DESC";

	public final static String LG_SCO_NAME = "LG_SCO_NAME";

	/* Course Management Panel */
	public final static String GUIC_ADD_COURSE = "GUIC_ADD_COURSE";
	public final static String GUIC_ADD_MAP = "GUIC_ADD_MAP";
	public final static String GUIC_COURSE_MANAGEMENT = "GUIC_COURSE_MANAGEMENT";

	public final static String GUIC_TLTP_DELETE_COURSE = "GUIC_TLTP_DELETE_COURSE";
	public final static String GUIC_TLTP_DELETE_MAP = "GUIC_TLTP_DELETE_MAP";
	public final static String GUIC_TLTP_EDIT_COURSE = "GUIC_TLTP_EDIT_COURSE";
	public final static String GUIC_TLTP_SCO_COURSE = "GUIC_TLTP_SCO_COURSE";

	public final static String GUIC_MSG_COURSE_DELETE = "GUIC_MSG_COURSE_DELETE";
	public final static String GUIC_MSG_COURSE_DELETE_NO_SCO = "GUIC_MSG_COURSE_DELETE_NO_SCO";
	public final static String GUIC_MSG_TTL_COURSE_DELETE = "GUIC_MSG_TTL_COURSE_DELETE";

	public final static String GUIC_NO_COURSES = "GUIC_NO_COURSES";

	/* Course Dialog */
	public final static String GUICDLG_COURSE_NAME = "GUICDLG_COURSE_NAME";
	public final static String GUICDLG_COURSE_DESCRIPTION = "GUICDLG_COURSE_DESCRIPTION";

	public final static String GUICDLG_TTL_ADD_COURSE = "GUICDLG_TTL_ADD_COURSE";
	public final static String GUICDLG_TTL_EDIT_COURSE = "GUICDLG_TTL_EDIT_COURSE";

	/* Sco Management Panel */
	public final static String GUIS_ADD_SCO = "GUIS_ADD_SCO";
	public final static String GUIS_LBL_SCO_OF_COURSE = "GUIS_LBL_SCO_OF_COURSE";
	public final static String GUIS_SCO_MANAGEMENT = "GUIS_SCO_MANAGEMENT";

	public final static String GUIS_TLTP_DELETE_SCO = "GUIS_TLTP_DELETE_SCO";
	public final static String GUIS_TLTP_EDIT_SCO = "GUIS_TLTP_EDIT_SCO";
	public final static String GUIS_TLTP_PARAMETERS_SCO = "GUIS_TLTP_PARAMETERS_SCO";
	public final static String GUIS_TLTP_COURSE_SCO = "GUIS_TLTP_COURSE_SCO";

	public final static String GUIS_MSG_SCO_DELETE = "GUIS_MSG_SCO_DELETE";
	public final static String GUIS_MSG_TTL_SCO_DELETE = "GUIS_MSG_TTL_SCO_DELETE";

	public final static String GUIS_NO_SCOS = "GUIS_NO_SCOS";

	/* Sco Dialog */
	public final static String GUISDLG_SCO_NAME = "GUISDLG_SCO_NAME";
	public final static String GUISDLG_SCO_DESCRIPTION = "GUISDLG_SCO_DESCRIPTION";

	public final static String GUISDLG_TTL_ADD_SCO = "GUISDLG_TTL_ADD_SCO";
	public final static String GUISDLG_TTL_EDIT_SCO = "GUISDLG_TTL_EDIT_SCO";

	public final static String GUISDLG_BTN_ADD_SCO = "GUISDLG_BTN_ADD_SCO";
	public final static String GUISDLG_BTN_PREVIEW_SCO = "GUISDLG_BTN_PREVIEW_SCO";
	public final static String GUISDLG_MSG_SELECT_SCO = "GUISDLG_MSG_SELECT_SCO";
	public final static String GUISDLG_MSG_NO_APPLETS = "GUISDLG_MSG_NO_APPLETS";
	public final static String GUISDLG_SHOW = "GUISDLG_SHOW";
	public final static String GUISDLG_ALL = "GUISDLG_ALL";
	public final static String GUISDLG_MSG_NO_SELECTION = "GUISDLG_MSG_NO_SELECTION";
	public final static String GUISDLG_RB_STANDARD_SCOS = "GUISDLG_RB_STANDARD_SCOS";
	public final static String GUISDLG_RB_OWN_SCOS = "GUISDLG_RB_OWN_SCOS";

	/* Parameter Management panel */
	public final static String GUIPA_BTN_PREVIEW = "GUIPA_BTN_PREVIEW";
	public final static String GUIPA_BTN_SAVE = "GUIPA_BTN_SAVE";
	public final static String GUIPA_BTN_RESET = "GUIPA_BTN_RESET";
	public final static String GUIPA_BTN_CANCEL = "GUIPA_BTN_CANCEL";
	public final static String GUIPA_DLG_TTL = "GUIPA_DLG_TTL";

	public final static String GUIPA_SCO_EDIT = "GUIPA_SCO_EDIT";

	public final static String GUIPA_NO_PARAMS = "GUIPA_NO_PARAMS";

	public final static String GUIPA_PARAMS_OF_SCO = "GUIPA_PARAMS_OF_SCO";

	public final static String GUIPA_MSG_PARAM_SAVE = "GUIPA_MSG_PARAM_SAVE";
	public final static String GUIPA_MSG_TTL_PARAM_SAVE = "GUIPA_MSG_TTL_PARAM_SAVE";

	public static final String GUIC_COURSE_SHARE = "GUIC_COURSE_SHARE";

	public static final String GUIS_SHOW_SCORE = "GUIS_SHOW_SCORE";

	public static final String GUIA_INSERT_SCOS = "GUIA_INSERT_SCOS";

	public static final String GUIH_STOP_EDIT = "GUIH_STOP_EDIT";

	public static final String GUIH_EDIT = "GUIH_EDIT";

	public static final String GUICDLG_MAP_NAME = "GUICDLG_MAP_NAME";

	public static final String GUIC_TLTP_EDIT_MAP = "GUIC_TLTP_EDIT_MAP";

	public static final String GUIPA_MSG_PARAM_UNSAFESAVE = "GUIPA_MSG_PARAM_UNSAFESAVE";

	public static final String GUIP_BTN_UNSAFESAVE = "UNSAFE SAVE";

	public static final String GUIRSDLG_MSG = "GUIRSDLG_MSG";

	public static final String GUIS_LOAD_LOGO = "GUIS_LOAD_LOGO";

	public static final String GUIEID_MSG1 = "GUIEID_MSG1";

	public static final String GUIEID_MSG2 = "GUIEID_MSG2";

	public static final String GUIEID_MSG3 = "GUIEID_MSG3";

	public static final String GUIEID_MSG4 = "GUIEID_MSG4";

	public static final String GUIUMP_REMOVE_CLASS = "GUIUMP_REMOVE_CLASS";
	public static final String GUIUMP_ALL_STUDENTS = "GUIUMP_ALL_STUDENTS";

	public static final String GUIC_SETTINGS = "GUIC_SETTINGS";
	public static final String GUIH_SETTINGS = "GUIH_SETTINGS";

	public static final String GUIC_SETTINGS_STUDENT = "GUIC_SETTINGS_STUDENT";
	public static final String GUIC_SETTINGS_TEACHER = "GUIC_SETTINGS_TEACHER";
	public static final String GUIC_SETTINGS_MODULE = "GUIC_SETTINGS_MODULE";

	public static final String GUICDLG_LICENCE = "GUICDLG_LICENCE";

	public static final String DWOAPPLET_EXISTS = "DWOAPPLET_EXISTS";
	private static ResourceBundle rb;

	private static String language;

	public static final String DWO_PROFILE_ADMIN = "DwoProfileAdmin";

	// Full Screen DWO
	public static final String FSD_TITLE = "FSD_TITLE";
	public static final String FSD_CONFIRM_TITLE = "FSD_CONFIRM_TITLE";
	public static final String FSD_ACTIVITEIT = "FSD_ACTIVITEIT";
	public static final String FSD_AFSLUITEN = "FSD_AFSLUITEN";
	public static final String FSD_SURE = "FSD_SURE";
	public static final String FSD_TIMEUP = "FSD_TIMEUP";
	public static final String FSD_START = "FSD_START";

	/**
     *
     */
	public TextMapper() {

	}

	public static ResourceBundle getResourceBundle() {
		if (rb == null) {
			if (language == null) {
				language = DEFAULT_LANGUAGE;
			}
			Locale lang = new Locale(language, "");

			String className = "fi.dwo.commons.system.text" + "." + TEXT_CLASS;
			// String className = TextMapper.class.getPackage().getName() + "."
			// + TEXT_CLASS;
			rb = ResourceBundle.getBundle(className, lang);
		}

		return rb;
	}

	/**
	 * @param text
	 * @return java.lang.String
	 *
	 */
	public static String getText(String text) {
		String result;
		result = getResourceBundle().getString(text);

		if (result == null) {
			return text;
		}
		return result;
	}

	/**
	 * Shortcut for MessageFormat.format(getText(key), ...);
	 *
	 * @param key
	 * @param params
	 * @return
	 */
	public static String format(String key, Object[] params) {
		return MessageFormat.format(getText(key), params);
	}

	/**
	 * @return Returns the language.
	 */
	public static String getLanguage() {
		if (language == null) {
			language = DEFAULT_LANGUAGE;
		}
		return language;
	}

	/**
	 * @param language
	 *            The language to set.
	 */
	public static void setLanguage(String language) {
		TextMapper.language = language;
		rb = null; // Wim: rb is een cache van de oude language
	}
}
