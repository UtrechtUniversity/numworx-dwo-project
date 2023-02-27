/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statically typed Dwo2 exception codes for which a localized description can
 * be found.
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public enum Dwo2ExceptionCode {
    Client_InternalError, //Internal software error, a stack trace should be acquired.
    User_AuthenticationError, //Illegal account details.
    User_IllegalAction, //Illegal action logged.
    User_AuthorizationError, // please switch role / switch class / switch login (not intentional illegal)
    User_NewPasswordsDoNotMatch, //Illegal action logged.
    User_Cancelled_RemoveTeacherFromSchoolClass,
    User_Cancelled_RemoveStudentFromSchoolClass,
    User_ConfirmNewLoginSession,
    User_ConfirmPasswordSwitch,
    User_ConfirmRoleSwitch,
    User_ConfirmSchoolClassSwitch,
    User_ConfirmSchoolDelete,
    User_ConfirmSchoolLoginDelete,
    User_ConfirmSingleSchoolStudentDelete,
    User_ConfirmRegularSchoolStudentDelete,
    User_ConfirmRegularSchoolTeacherDelete,
    User_ConfirmTeacherFromSchoolDelete,
    User_ConfirmSchoolAdminFromSchoolDelete,
    User_ConfirmDeleteMultiUsersFromSchool,
    User_NotAValidDateString,
    User_NotAValidDateValue,
    User_Q_ForgotPassword,
    Exam_AuthenticationError, //Illegal password exam.
    User_AuthenticationCancelled,
    Exam_InvalidSession,

    //PersistentId errors
    PersistentId_ConversionError,
    
    //REST interface errors
    Rest_ConnectionTimeout, //Connection time-out to REST-interface.
    Rest_CanNotReachServer, //Server did not respond
    Rest_InternalError, //Internal software error, a stack trace should be acquired.
    Rest_InterfaceError, // Error due to an improper REST-interface.
    Rest_FormatError, // Usually error in the object data send in the message body 
    Rest_UnsupportedFunction,
    Rest_StudentScoExists,
    Rest_SchoolclassDoesNotExist,
    Rest_ObjectAlreadyExists,
    Rest_ObjectModified,
    Rest_ScoNameExists, // add sco contraint failed. rollback occurred, possible cause duplicate sconame
    Rest_CourseNameExists,
    Rest_CanNotAddStudentToClass,
    Rest_NameTooLong, // if coursename,sconame,classname, etc. longer than mysql supports
    // REST LoginContext errors
    Rest_LoginContext_exists,
    Rest_No_LoginContext_exists,
    Rest_LoginNeeded, // no public access to non-public resources.

    // REST Registration errors
    Rest_Registration_UserName_exists, //User exists already, can't register.
    Rest_Registration_UserNames_exists, //Users exists already, can't register.
    Rest_Registration_Invalid_Full_Name, //User exists already, can't register.
    Rest_Registration_Invalid_school_role_credentials, //Illegal combination of school login and passcode.
    Rest_Registration_Invalid_schoolclass_registration_key, //Illegal combination of school login and passcode.
    Rest_Registration_School_authentication_failed,
    Rest_Registration_School_license_expired,
    Rest_Registration_hasRole_exists, //User is already registered for this role.
    Rest_Registration_Email_Address_Invalid, //Invalid characters
    Rest_Registration_UserName_Invalid, //Invalid characters
    Rest_Submitted_SchoolClass_exists,//A school
    Rest_Registration_Password_Invalid, 
    Rest_Active_SchoolClass_Not_Set, //schoolclassid in hasRole is null!
    //GUI
    GUI_NoUserIsSignedIn,
    GUI_AnIncorrectPasswordWasGiven,
    GUI_BTN_deleteFromSchool, 
    GUI_BTN_toggleSelect, 
    Rest_Registration_Required_Fields,
    Rest_StudentModelNotAvailable, 
    Rest_StudentModelNotSet,
    Rest_ResourceNotFound
}
