/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.locale;


/**
 * Statically typed Dwo2 exception codes for which a localized description can
 * be found.
 *
 * @author Gert van der Plas
 */
public enum Dwo2LocaleMessageCode {
//App names
NUM_APP_TEACHER,
NUM_APP_STUDENT,
NUM_APP_SCHOOLADMIN,    
NUM_APP_DWOADMIN,    
    
//generic buttons
NUM_BTN_Ok,  
NUM_BTN_Cancel,
NUM_BTN_Add,
NUM_BTN_Save,
NUM_BTN_Edit,
NUM_BTN_Apply,
NUM_BTN_Confirm,
NUM_BTN_Abort,
NUM_BTN_Update,
NUM_BTN_Remove,
NUM_BTN_Show,
NUM_BTN_Search,
NUM_BTN_Filter,
NUM_BTN_Reset,
NUM_BTN_Modify,
NUM_BTN_Connect,
NUM_BTN_RefreshList,
NUM_BTN_CopyLeft,
NUM_BTN_MoveLeft,
NUM_BTN_CopyRight,
NUM_BTN_MoveRight,
 

//dialogs
NUM_DLG_User_Progress,
NUM_DLG_User_Alert,
NUM_DLG_User_Message,
NUM_DLG_User_ConfirmLogout,
NUM_DLG_User_NoTeacher,
NUM_DLG_User_NoAccessForYourAccount,
NUM_DLG_User_ConfirmSchoolLoginDelete,
NUM_DLG_User_ConfirmChangeCommited,
NUM_DLG_User_ConfirmPasswordSwitch,
NUM_DLG_Class_ConfirmChangeCommited,
NUM_DLG_User_StudentAdded,
NUM_DLG_Class_Removed,
NUM_DLG_Class_ConfirmRemoveSchoolClass,
NUM_DLG_Class_CopyingStudentsTitle,
NUM_DLG_MovingStudentsTitle,
NUM_DLG_Class_CopyingStudentsCompleted,
NUM_DLG_Class_MovingStudentsCompleted,
NUM_DLG_Class_CopyingStudents,
NUM_DLG_Class_MovingStudents,
NUM_DLG_Class_StartingCopyStudents,
NUM_DLG_Class_StartingMovingStudents,
NUM_DLG_Results_ConfirmClearingStudentResults,
NUM_DLG_Results_StartClearingStudentResults,
NUM_DLG_Results_ClearingStudentResults,
NUM_DLG_Results_ClearingStudentResultsCompleted,


//generic labels
 NUM_LBL_YES,
 NUM_LBL_NO,
 NUM_LBL_ON,
 NUM_LBL_OFF,
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
 NUM_LBL_MODULES,
 NUM_LBL_STUDENTS,
 NUM_LBL_TEACHERS,
 NUM_LBL_ROLE,
 NUM_LBL_USERNAME, 
 NUM_LBL_PASSWORD,
 NUM_TBL_USERNAME,
 NUM_TBL_GIVENNAME,
 NUM_TBL_INSERTION,
 NUM_TBL_INSERTION_SHORT,
 NUM_TBL_FAMILYNAME,
 NUM_TBL_SINGLE_SCHOOL,
 NUM_TBL_EMAIL,
 NUM_TBL_PASSWORD,
 NUM_TBL_ROLE,
 NUM_TBL_EDITOPTIONS,
 NUM_TBL_SELECT,
 NUM_TBL_STUDENTS,
 NUM_TBL_TEACHERS,
 NUM_TBL_MODULES,
 NUM_TBL_EMPTYTABLE,
 NUM_TBL_FETCHINGDATA,

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
 
//klassenbeheer
 NUM_PNL_CLASSES,

 NUM_SEC_CLASSES_EDIT,
 NUM_SEC_CLASSES_ADD,
 
 NUM_LBL_CLASSES_CLASSNAME,
 NUM_LBL_CLASSES_SELECTTOEDIT,
 NUM_TBL_CLASSES_CLASSNAME,
 NUM_LBL_CLASSES_TREESTRUCT,
 NUM_LBL_CLASSES_ACCESSKEY,
 
 //Klasbeheer
 NUM_LBL_ClassAssignments,
 NUM_BTN_MOVECOPY, 
 NUM_BTN_ShowStudents,
 NUM_BTN_ShowTeachers,
 NUM_BTN_ShowModules,
 NUM_BTN_AddStudents,
 NUM_BTN_AddTeachers,
 NUM_BTN_AssignModules,

 //add student to class
 NUM_SEC_AssignStudentToClass,
 
 
 //add teacher to class
 NUM_SEC_AssignTeacherToClass,
 
 //add modules to class
 NUM_SEC_AssignModulesToClass,
 NUM_LBL_AssignModulesToClass,
 NUM_LBL_ModulesVisibleForClass,
 NUM_CLASSMODULES_Visible,
 NUM_CLASSMODULES_InVisible,
 NUM_CLASSMODULES_NeverAssigned,
 NUM_CLASSMODULES_FindAModule,
 NUM_CLASSMODULES_SettingsModules,
 NUM_CLASSMODULES_AccessPeriod,
 NUM_CLASSMODULES_CourseType,
 
//move copy students
NUM_PNL_CLASSEDITSTUDENTS,
NUM_SEC_CLASSEDITSTUDENTS,
NUM_LBL_CLASSEDITSTUDENTS_STUDENTSLEFTCLASS,
NUM_LBL_CLASSEDITSTUDENTS_STUDENTSRIGHTCLASS,
NUM_LBL_CLASSEDITSTUDENTS_MSG,
NUM_BTN_CLASSEDITSTUDENTS_SELECT2NDCLASS,

//Persons
NUM_PNL_PERSONS_MANAGE,
NUM_SEC_PERSONS_MANAGE,
NUM_LBL_PERSONS_FOUND,
NUM_BTN_PERSONS_EDIT,
NUM_SEC_PERSONS_ADD,
NUM_LBL_PERSONS_ADD,
NUM_BTN_PERSONS_ADD,
NUM_LBL_Q_WHICH2ROLES,

//Edit Persons Teacher/Student
NUM_DLG_EDITSTUDENT_Q_RemoveClassFromStudent,
NUM_DLG_EDITTEACHER_Q_RemoveClassFromTeacher,
NUM_SEC_PERSONS_EDIT,
NUM_SEC_PERSONS_EDITCLASSES,

//SelectedResults
NUM_LBL_SELECTEDRESULTS_BACKTOSELECT,
NUM_TBL_SELECTEDRESULTS_AllModules,
NUM_LBL_SELECTEDRESULTS_ClearResults,

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
