package nl.uu.fi.dwo.rest.locale;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'/Users/peterboon/Documents/workspace-neon/DWO-rest-lib/target/classes/nl/uu/fi/dwo/rest/locale/Dwo2ExceptionMessages.properties'.
 */
public interface Dwo2ExceptionMessages extends com.google.gwt.i18n.client.ConstantsWithLookup {
  
  /**
   * Translated "An incorrect password was given.".
   * 
   * @return translated "An incorrect password was given."
   */
  @DefaultStringValue("An incorrect password was given.")
  @Key("Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven")
  String Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven();

  /**
   * Translated "delete from school".
   * 
   * @return translated "delete from school"
   */
  @DefaultStringValue("delete from school")
  @Key("Dwo2ExceptionCode.GUI_BTN_deleteFromSchool")
  String Dwo2ExceptionCode_GUI_BTN_deleteFromSchool();

  /**
   * Translated "(de)select all".
   * 
   * @return translated "(de)select all"
   */
  @DefaultStringValue("(de)select all")
  @Key("Dwo2ExceptionCode.GUI_BTN_toggleSelect")
  String Dwo2ExceptionCode_GUI_BTN_toggleSelect();

  /**
   * Translated "No user is signed in.".
   * 
   * @return translated "No user is signed in."
   */
  @DefaultStringValue("No user is signed in.")
  @Key("Dwo2ExceptionCode.GUI_NoUserIsSignedIn")
  String Dwo2ExceptionCode_GUI_NoUserIsSignedIn();

  /**
   * Translated "You have not yet selected a school class.".
   * 
   * @return translated "You have not yet selected a school class."
   */
  @DefaultStringValue("You have not yet selected a school class.")
  @Key("Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set")
  String Dwo2ExceptionCode_Rest_Active_SchoolClass_Not_Set();

  /**
   * Translated "Can not contact the server, please try  again before reporting.".
   * 
   * @return translated "Can not contact the server, please try  again before reporting."
   */
  @DefaultStringValue("Can not contact the server, please try  again before reporting.")
  @Key("Dwo2ExceptionCode.Rest_ConnectionTimeout")
  String Dwo2ExceptionCode_Rest_ConnectionTimeout();

  /**
   * Translated "Incorrect formatted REST-request.".
   * 
   * @return translated "Incorrect formatted REST-request."
   */
  @DefaultStringValue("Incorrect formatted REST-request.")
  @Key("Dwo2ExceptionCode.Rest_FormatError")
  String Dwo2ExceptionCode_Rest_FormatError();

  /**
   * Translated "Invalid REST-request.".
   * 
   * @return translated "Invalid REST-request."
   */
  @DefaultStringValue("Invalid REST-request.")
  @Key("Dwo2ExceptionCode.Rest_InterfaceError")
  String Dwo2ExceptionCode_Rest_InterfaceError();

  /**
   * Translated "An internal error occurred, please try again before reporting.".
   * 
   * @return translated "An internal error occurred, please try again before reporting."
   */
  @DefaultStringValue("An internal error occurred, please try again before reporting.")
  @Key("Dwo2ExceptionCode.Rest_InternalError")
  String Dwo2ExceptionCode_Rest_InternalError();

  /**
   * Translated "You entered an invalid email address string.".
   * 
   * @return translated "You entered an invalid email address string."
   */
  @DefaultStringValue("You entered an invalid email address string.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid")
  String Dwo2ExceptionCode_Rest_Registration_Email_Address_Invalid();

  /**
   * Translated "Illegal combination of school login and pass code. This will be logged.".
   * 
   * @return translated "Illegal combination of school login and pass code. This will be logged."
   */
  @DefaultStringValue("Illegal combination of school login and pass code. This will be logged.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials")
  String Dwo2ExceptionCode_Rest_Registration_Invalid_school_role_credentials();

  /**
   * Translated "The registration key for this school class is invalid.".
   * 
   * @return translated "The registration key for this school class is invalid."
   */
  @DefaultStringValue("The registration key for this school class is invalid.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Invalid_schoolclass_registration_key")
  String Dwo2ExceptionCode_Rest_Registration_Invalid_schoolclass_registration_key();

  /**
   * Translated "The password format is invalid.".
   * 
   * @return translated "The password format is invalid."
   */
  @DefaultStringValue("The password format is invalid.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Password_Invalid")
  String Dwo2ExceptionCode_Rest_Registration_Password_Invalid();

  /**
   * Translated "Required fields are empty".
   * 
   * @return translated "Required fields are empty"
   */
  @DefaultStringValue("Required fields are empty")
  @Key("Dwo2ExceptionCode.Rest_Registration_Required_Fields")
  String Dwo2ExceptionCode_Rest_Registration_Required_Fields();

  /**
   * Translated "School authentication is incorrect.".
   * 
   * @return translated "School authentication is incorrect."
   */
  @DefaultStringValue("School authentication is incorrect.")
  @Key("Dwo2ExceptionCode.Rest_Registration_School_authentication_failed")
  String Dwo2ExceptionCode_Rest_Registration_School_authentication_failed();

  /**
   * Translated "The school license has expired. You can register\n once it has renewed. Contact the school.".
   * 
   * @return translated "The school license has expired. You can register\n once it has renewed. Contact the school."
   */
  @DefaultStringValue("The school license has expired. You can register\n once it has renewed. Contact the school.")
  @Key("Dwo2ExceptionCode.Rest_Registration_School_license_expired")
  String Dwo2ExceptionCode_Rest_Registration_School_license_expired();

  /**
   * Translated "Invalid username, it may contain one or more illegal characters.".
   * 
   * @return translated "Invalid username, it may contain one or more illegal characters."
   */
  @DefaultStringValue("Invalid username, it may contain one or more illegal characters.")
  @Key("Dwo2ExceptionCode.Rest_Registration_UserName_Invalid")
  String Dwo2ExceptionCode_Rest_Registration_UserName_Invalid();

  /**
   * Translated "That username already exists in the database.".
   * 
   * @return translated "That username already exists in the database."
   */
  @DefaultStringValue("That username already exists in the database.")
  @Key("Dwo2ExceptionCode.Rest_Registration_UserName_exists")
  String Dwo2ExceptionCode_Rest_Registration_UserName_exists();

  /**
   * Translated "The remaining users can not be created. Their usernames already exist.".
   * 
   * @return translated "The remaining users can not be created. Their usernames already exist."
   */
  @DefaultStringValue("The remaining users can not be created. Their usernames already exist.")
  @Key("Dwo2ExceptionCode.Rest_Registration_UserNames_exists")
  String Dwo2ExceptionCode_Rest_Registration_UserNames_exists();

  /**
   * Translated "You are already registered for that combination of school and role. ".
   * 
   * @return translated "You are already registered for that combination of school and role. "
   */
  @DefaultStringValue("You are already registered for that combination of school and role. ")
  @Key("Dwo2ExceptionCode.Rest_Registration_hasRole_exists")
  String Dwo2ExceptionCode_Rest_Registration_hasRole_exists();

  /**
   * Translated "A school class with that name already exists within the school.".
   * 
   * @return translated "A school class with that name already exists within the school."
   */
  @DefaultStringValue("A school class with that name already exists within the school.")
  @Key("Dwo2ExceptionCode.Rest_Submitted_SchoolClass_exists")
  String Dwo2ExceptionCode_Rest_Submitted_SchoolClass_exists();

  /**
   * Translated "Invalid authentication details.".
   * 
   * @return translated "Invalid authentication details."
   */
  @DefaultStringValue("Invalid authentication details.")
  @Key("Dwo2ExceptionCode.User_AuthenticationError")
  String Dwo2ExceptionCode_User_AuthenticationError();

  /**
   * Translated "Do you want to remove the selected students and pertaining data from the school?\nStudent accounts are also deleted.".
   * 
   * @return translated "Do you want to remove the selected students and pertaining data from the school?\nStudent accounts are also deleted."
   */
  @DefaultStringValue("Do you want to remove the selected students and pertaining data from the school?\nStudent accounts are also deleted.")
  @Key("Dwo2ExceptionCode.User_ConfirmDeleteMultiUsersFromSchool")
  String Dwo2ExceptionCode_User_ConfirmDeleteMultiUsersFromSchool();

  /**
   * Translated "You did not logout in your last session.\nEnsure that you have closed all your other DWO-applications before\ncontinuing to prevent data corruption. Continue?".
   * 
   * @return translated "You did not logout in your last session.\nEnsure that you have closed all your other DWO-applications before\ncontinuing to prevent data corruption. Continue?"
   */
  @DefaultStringValue("You did not logout in your last session.\nEnsure that you have closed all your other DWO-applications before\ncontinuing to prevent data corruption. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmNewLoginSession")
  String Dwo2ExceptionCode_User_ConfirmNewLoginSession();

  /**
   * Translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmPasswordSwitch")
  String Dwo2ExceptionCode_User_ConfirmPasswordSwitch();

  /**
   * Translated "Do you want to remove access for account {0}\n from the school and delete all school-related data irrevocably?".
   * 
   * @return translated "Do you want to remove access for account {0}\n from the school and delete all school-related data irrevocably?"
   */
  @DefaultStringValue("Do you want to remove access for account {0}\n from the school and delete all school-related data irrevocably?")
  @Key("Dwo2ExceptionCode.User_ConfirmRegularSchoolStudentDelete")
  String Dwo2ExceptionCode_User_ConfirmRegularSchoolStudentDelete();

  /**
   * Translated "Ensure that you have closed all your other DWO-applications\nbefore continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your other DWO-applications\nbefore continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your other DWO-applications\nbefore continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmRoleSwitch")
  String Dwo2ExceptionCode_User_ConfirmRoleSwitch();

  /**
   * Translated "Do you want to remove schooladmin {0}\n and pertaining data irrevocably from the school?".
   * 
   * @return translated "Do you want to remove schooladmin {0}\n and pertaining data irrevocably from the school?"
   */
  @DefaultStringValue("Do you want to remove schooladmin {0}\n and pertaining data irrevocably from the school?")
  @Key("Dwo2ExceptionCode.User_ConfirmSchoolAdminFromSchoolDelete")
  String Dwo2ExceptionCode_User_ConfirmSchoolAdminFromSchoolDelete();

  /**
   * Translated "Ensure that you have closed all your DWO-applications\nbefore continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your DWO-applications\nbefore continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your DWO-applications\nbefore continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmSchoolClassSwitch")
  String Dwo2ExceptionCode_User_ConfirmSchoolClassSwitch();

  /**
   * Translated "Do you want to delete school with schoollogin \"{0}\" \nand delete all pertaining data irrevocably?".
   * 
   * @return translated "Do you want to delete school with schoollogin \"{0}\" \nand delete all pertaining data irrevocably?"
   */
  @DefaultStringValue("Do you want to delete school with schoollogin \"{0}\" \nand delete all pertaining data irrevocably?")
  @Key("Dwo2ExceptionCode.User_ConfirmSchoolDelete")
  String Dwo2ExceptionCode_User_ConfirmSchoolDelete();

  /**
   * Translated "Do you want to remove the school login at school {0} as {1}\n and delete all pertaining data irrevocably?".
   * 
   * @return translated "Do you want to remove the school login at school {0} as {1}\n and delete all pertaining data irrevocably?"
   */
  @DefaultStringValue("Do you want to remove the school login at school {0} as {1}\n and delete all pertaining data irrevocably?")
  @Key("Dwo2ExceptionCode.User_ConfirmSchoolLoginDelete")
  String Dwo2ExceptionCode_User_ConfirmSchoolLoginDelete();

  /**
   * Translated "Do you want to delete the student account {0}\n and all its data irrevocably?".
   * 
   * @return translated "Do you want to delete the student account {0}\n and all its data irrevocably?"
   */
  @DefaultStringValue("Do you want to delete the student account {0}\n and all its data irrevocably?")
  @Key("Dwo2ExceptionCode.User_ConfirmSingleSchoolStudentDelete")
  String Dwo2ExceptionCode_User_ConfirmSingleSchoolStudentDelete();

  /**
   * Translated "Do you want to remove teacher {0}\n and delete all pertaining data from the school?".
   * 
   * @return translated "Do you want to remove teacher {0}\n and delete all pertaining data from the school?"
   */
  @DefaultStringValue("Do you want to remove teacher {0}\n and delete all pertaining data from the school?")
  @Key("Dwo2ExceptionCode.User_ConfirmTeacherFromSchoolDelete")
  String Dwo2ExceptionCode_User_ConfirmTeacherFromSchoolDelete();

  /**
   * Translated "Illegal REST-request, the details of this action are logged.".
   * 
   * @return translated "Illegal REST-request, the details of this action are logged."
   */
  @DefaultStringValue("Illegal REST-request, the details of this action are logged.")
  @Key("Dwo2ExceptionCode.User_IllegalAction")
  String Dwo2ExceptionCode_User_IllegalAction();

  /**
   * Translated "New passwords do not match.".
   * 
   * @return translated "New passwords do not match."
   */
  @DefaultStringValue("New passwords do not match.")
  @Key("Dwo2ExceptionCode.User_NewPasswordsDoNotMatch")
  String Dwo2ExceptionCode_User_NewPasswordsDoNotMatch();

  /**
   * Translated "forgot password?".
   * 
   * @return translated "forgot password?"
   */
  @DefaultStringValue("forgot password?")
  @Key("Dwo2ExceptionCode.User_Q_ForgotPassword")
  String Dwo2ExceptionCode_User_Q_ForgotPassword();
}
