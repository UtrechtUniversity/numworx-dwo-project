/*Copyrighted 2015. */
package fi.dwo.commons.exceptions;

/**
 * Statically typed Dwo2 exception codes for which a localized description can be found.
 * 
 * @author Gert van der Plas
 */
public enum Dwo2ExceptionCode {
    Rest_InternalError, //Internal software error, a stack trace should be acquired.
    User_AuthenticationError, // Illegal account details.
    Rest_InterfaceError, // Error due to an improper Rest-interface.
    
    //Registration errors
    Rest_Registration_UserName_exists //username
}
