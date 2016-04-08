/*Copyrighted 2015. */
package fi.dwo.rest.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statically typed Dwo2 exception codes for which a localized description can be found.
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public enum Dwo2ExceptionCode {
    User_AuthenticationError, //Illegal account details.
     User_IllegalAction, //Illegal action logged.

    //REST interface errors
    Rest_InternalError, //Internal software error, a stack trace should be acquired.
    Rest_InterfaceError, // Error due to an improper REST-interface.
    Rest_ConnectionTimeout, //Connection time-out to REST-interface.

    // REST Registration errors
    Rest_Registration_UserName_exists,  //User exists already, can't register.
    Rest_Registration_Invalid_school_role_credentials, //Illegal combination of school login and passcode.
    Rest_Registration_School_authentication_failed, 
    Rest_Registration_School_license_expired,  
    Rest_Registration_hasRole_exists, //User is already registered for this role.
    
}
