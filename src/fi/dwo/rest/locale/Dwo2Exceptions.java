package fi.dwo.rest.locale;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/Users/gert/NetBeansProjects/UU/DWO-gwt-lib/trunk/src/fi/dwo/rest/locale/Dwo2Exceptions.properties'.
 */
public interface Dwo2Exceptions extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "An incorrect password was given.".
   * 
   * @return translated "An incorrect password was given."
   */
  @DefaultMessage("An incorrect password was given.")
  @Key("Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven")
  String Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven();

  /**
   * Translated "No user is signed in.".
   * 
   * @return translated "No user is signed in."
   */
  @DefaultMessage("No user is signed in.")
  @Key("Dwo2ExceptionCode.GUI_NoUserIsSignedIn")
  String Dwo2ExceptionCode_GUI_NoUserIsSignedIn();

  /**
   * Translated "Can not contact the server, please try  again before reporting.".
   * 
   * @return translated "Can not contact the server, please try  again before reporting."
   */
  @DefaultMessage("Can not contact the server, please try  again before reporting.")
  @Key("Dwo2ExceptionCode.Rest_ConnectionTimeout")
  String Dwo2ExceptionCode_Rest_ConnectionTimeout();

  /**
   * Translated "Invalid REST-request.".
   * 
   * @return translated "Invalid REST-request."
   */
  @DefaultMessage("Invalid REST-request.")
  @Key("Dwo2ExceptionCode.Rest_InterfaceError")
  String Dwo2ExceptionCode_Rest_InterfaceError();

  /**
   * Translated "An internal error occurred, please try again before reporting.".
   * 
   * @return translated "An internal error occurred, please try again before reporting."
   */
  @DefaultMessage("An internal error occurred, please try again before reporting.")
  @Key("Dwo2ExceptionCode.Rest_InternalError")
  String Dwo2ExceptionCode_Rest_InternalError();

  /**
   * Translated "Illegal combination of school login and pass code. This will be logged.".
   * 
   * @return translated "Illegal combination of school login and pass code. This will be logged."
   */
  @DefaultMessage("Illegal combination of school login and pass code. This will be logged.")
  @Key("Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials")
  String Dwo2ExceptionCode_Rest_Registration_Invalid_school_role_credentials();

  /**
   * Translated "School authentication is incorrect.".
   * 
   * @return translated "School authentication is incorrect."
   */
  @DefaultMessage("School authentication is incorrect.")
  @Key("Dwo2ExceptionCode.Rest_Registration_School_authentication_failed")
  String Dwo2ExceptionCode_Rest_Registration_School_authentication_failed();

  /**
   * Translated "The school license has expired. You can register once it has renewed. Contact the school.".
   * 
   * @return translated "The school license has expired. You can register once it has renewed. Contact the school."
   */
  @DefaultMessage("The school license has expired. You can register once it has renewed. Contact the school.")
  @Key("Dwo2ExceptionCode.Rest_Registration_School_license_expired")
  String Dwo2ExceptionCode_Rest_Registration_School_license_expired();

  /**
   * Translated "That username already exists in the database.".
   * 
   * @return translated "That username already exists in the database."
   */
  @DefaultMessage("That username already exists in the database.")
  @Key("Dwo2ExceptionCode.Rest_Registration_UserName_exists")
  String Dwo2ExceptionCode_Rest_Registration_UserName_exists();

  /**
   * Translated "You are already registered for that combination of school and role. ".
   * 
   * @return translated "You are already registered for that combination of school and role. "
   */
  @DefaultMessage("You are already registered for that combination of school and role. ")
  @Key("Dwo2ExceptionCode.Rest_Registration_hasRole_exists")
  String Dwo2ExceptionCode_Rest_Registration_hasRole_exists();

  /**
   * Translated "Invalid authentication details.".
   * 
   * @return translated "Invalid authentication details."
   */
  @DefaultMessage("Invalid authentication details.")
  @Key("Dwo2ExceptionCode.User_AuthenticationError")
  String Dwo2ExceptionCode_User_AuthenticationError();

  /**
   * Translated "Illegal REST-request, the details of this action are logged.".
   * 
   * @return translated "Illegal REST-request, the details of this action are logged."
   */
  @DefaultMessage("Illegal REST-request, the details of this action are logged.")
  @Key("Dwo2ExceptionCode.User_IllegalAction")
  String Dwo2ExceptionCode_User_IllegalAction();
}
