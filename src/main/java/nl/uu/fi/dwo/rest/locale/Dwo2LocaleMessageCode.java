/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.locale;


/**
 * Statically typed Dwo2 exception codes for which a localized description can
 * be found.
 *
 * @author Gert van der Plas
 */
public enum Dwo2LocaleMessageCode {
//generic buttons
NUM_BTN_Ok,  
NUM_BTN_Cancel,
NUM_BTN_Save,
NUM_BTN_Apply,
NUM_BTN_Confirm,
NUM_BTN_Abort,
NUM_BTN_Update,

//dialogs
NUM_DLG_User_ConfirmLogout,
NUM_DLG_User_NoTeacher,
NUM_DLG_User_ConfirmSchoolLoginDelete,
NUM_DLG_User_ConfirmChangeCommited,
NUM_DLG_User_ConfirmPasswordSwitch,

//generic labels
 NUM_LBL_STUDENTLETTER,
 NUM_LBL_TEACHERLETTER,
 NUM_LBL_SCHOOLADMINLETTER,
 NUM_LBL_DWOADMIN,
 NUM_LBL_STUDENT,
 NUM_LBL_TEACHER,
 NUM_LBL_SCHOOLADMIN,
 NUM_LBL_ADMIN,
 NUM_LBL_CUR_PASSWORD,
 NUM_LBL_NEW_PASSWORD,
 NUM_LBL_RPT_NEW_PASSWORD,


 NUM_TBL_USERNAME,
 NUM_TBL_GIVENNAME,
 NUM_TBL_INSERTION,
 NUM_TBL_FAMILYNAME,
 NUM_TBL_SINGLE_SCHOOL,
 NUM_TBL_EMAIL,
 NUM_TBL_ROLE,

//login scherm
 NUM_BTN_LOGIN,
 NUM_LBL_Q_PASWORD_FORGOTTEN,
 NUM_LBL_Q_NO_ACCOUNT,
 NUM_LBL_MORE_ON_NUMWORX,
 NUM_LBL_MANAGE_ACCOUNT,
 NUM_LBL_LOGOUT,

//main menu
 NUM_MNU_RESULTS,
 NUM_MNU_PEOPLE,
 MUM_MNU_CLASSES,
 NUM_MNU_MODULES,
 NUM_MNU_ORGANIZATION,
 NUM_MNU_HELP,

//account
 NUM_PNL_ACCOUNT,

 NUM_SEC_ACCOUNT_CHANGE,
 NUM_SEC_ACCOUNT_SCHOOLLOGINS,
 NUM_SEC_ACCOUNT_SCHOOLLOGIN_ADD,

 NUM_LBL_SCHOOLLOGIN,
 NUM_LBL_SCHOOLPASSWORD,
 NUM_LBL_Q_WHICHROLE,
 NUM_TBL_SCHOOLLOGINOPTION,
 NUM_TBL_STARTWITHSCHOOLLOGIN,
 NUM_TBL_REMOVESCHOOLLOGIN,
 
 
/** Old tags for java */
GUI_Button_Ok,  
GUI_Button_Cancel,
GUI_Button_Save,
GUI_Button_Apply,
GUI_Button_Confirm,
GUI_Button_Abort,
GUI_Button_Update,
GUI_Button_AddTeachersToClass,
GUI_Button_StudentModels,
GUI_Delete,
GUI_SchoolLogin,
GUI_Login,
GUI_SchoolclassName,
GUI_Username,
GUI_Password,
GUI_GivenName,
GUI_Insertion,
GUI_FamilyName,
GUI_Email,
GUI_NewPassword,
GUI_NewPasswordAgain,
GUI_NewSchoolLogin,
GUI_NoUserIsSignedIn,
GUI_AnIncorrectPasswordWasGiven,
GUI_MyProfile,
GUI_MySchoolLogins,
GUI_MySchoolClasses,
GUI_SchoolName,
GUI_RoleName,
GUI_SchoolClassRegistrationKey,
GUI_SwitchSchool,
GUI_SwitchTeacher,
GUI_UserRegistrationSucceeded,
GUI_UserRegistrationFailed,
GUI_DLG_User_ConfirmNewLoginSession,
GUI_DLG_User_ConfirmPasswordSwitch,
GUI_DLG_User_ConfirmRoleSwitch,
GUI_DLG_User_ConfirmSchoolClassSwitch,
GUI_DLG_User_ConfirmSchoolDelete,
GUI_DLG_User_ConfirmSchoolLoginDelete,
GUI_DLG_User_ConfirmLogout,
GUI_DLG_User_ConfirmChangeCommited,
GUI_DLG_User_ConfirmSingleSchoolStudentDelete,
GUI_DLG_User_ConfirmRegularSchoolStudentDelete,
GUI_DLG_User_ConfirmTeacherFromSchoolDelete,
GUI_DLG_User_ConfirmSchoolAdminFromSchoolDelete,
GUI_DLG_User_ConfirmDeleteMultiUsersFromSchool,

STUDENT,
TEACHER,
NULLSCHOOL,
ADMIN,
SCHOOLADMIN,
SCHOOLCODE,
GUIR_MSG_PROVIDED_SCHOOL

}
