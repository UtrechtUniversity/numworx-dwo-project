/*Copyrighted 2015. */
package fi.dwo.commons.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statically typed Dwo2 exception codes for which a localized description can be found.
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public enum Dwo2ExceptionCode {
    Rest_InternalError, //Internal software error, a stack trace should be acquired.
    User_AuthenticationError, // Illegal account details.
    Rest_InterfaceError, // Error due to an improper Rest-interface.
    
    // Registration errors
    Rest_Registration_UserName_exists,  //User exists already, can't register.
    Rest_Registration_Invalid_school_role_credentials, //Illegal combination of school login and passcode.
    Rest_Registration_School_license_expired, 
    Rest_Registration_hasRole_exists //
}
