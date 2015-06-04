/*Copyrighted 2015. */
package fi.dwo.commons.exceptions;

/**
 * Exception codes for which a Localized explanation can be found.
 * @author Gert van der Plas
 */
public enum Dwo2ExceptionCode {
    Rest_InternalError, //Internal software error, a stack trace should be acquired.
    User_AuthenticationError, // Illegal account details.
    Rest_InterfaceError, // Error due to an improper Rest-interface.
    
    //Registration errors
    Rest_Registration_UserName_exists //username
}
