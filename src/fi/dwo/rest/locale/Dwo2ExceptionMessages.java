package fi.dwo.rest.locale;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'C:/Users/wim/workspace-luna/DWO-gwt-lib/src/fi/dwo/rest/locale/Dwo2ExceptionMessages.properties'.
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
   * Translated "No user is signed in.".
   * 
   * @return translated "No user is signed in."
   */
  @DefaultStringValue("No user is signed in.")
  @Key("Dwo2ExceptionCode.GUI_NoUserIsSignedIn")
  String Dwo2ExceptionCode_GUI_NoUserIsSignedIn();

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
   * Translated "The password length exceeds 128 characters.".
   * 
   * @return translated "The password length exceeds 128 characters."
   */
  @DefaultStringValue("The password length exceeds 128 characters.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Password_Invalid")
  String Dwo2ExceptionCode_Rest_Registration_Password_Invalid();

  /**
   * Translated "School authentication is incorrect.".
   * 
   * @return translated "School authentication is incorrect."
   */
  @DefaultStringValue("School authentication is incorrect.")
  @Key("Dwo2ExceptionCode.Rest_Registration_School_authentication_failed")
  String Dwo2ExceptionCode_Rest_Registration_School_authentication_failed();

  /**
   * Translated "The school license has expired. You can register once it has renewed. Contact the school.".
   * 
   * @return translated "The school license has expired. You can register once it has renewed. Contact the school."
   */
  @DefaultStringValue("The school license has expired. You can register once it has renewed. Contact the school.")
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
   * Translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmPasswordSwitch")
  String Dwo2ExceptionCode_User_ConfirmPasswordSwitch();

  /**
   * Translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your other DWO-applications before continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmRoleSwitch")
  String Dwo2ExceptionCode_User_ConfirmRoleSwitch();

  /**
   * Translated "Ensure that you have closed all your DWO-applications before continuing to prevent data loss. Continue?".
   * 
   * @return translated "Ensure that you have closed all your DWO-applications before continuing to prevent data loss. Continue?"
   */
  @DefaultStringValue("Ensure that you have closed all your DWO-applications before continuing to prevent data loss. Continue?")
  @Key("Dwo2ExceptionCode.User_ConfirmSchoolClassSwitch")
  String Dwo2ExceptionCode_User_ConfirmSchoolClassSwitch();

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
}
