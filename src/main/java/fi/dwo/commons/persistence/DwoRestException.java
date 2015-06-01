/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

import fi.dwo.commons.system.TextMapper;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Persistence error message. This exception contains an error type that can be
 * localized for translation. The message part is not localized as it is assumed
 * to contain stack traces and other internal code info. For GUI messaging it is
 * recommended to use the method getLocalizedTypeMessage.
 *
 * @author G.A.J. van der Plas
 */
public class DwoRestException extends Exception {

    public static enum RestErrorType { //enumName maps to DwoRestException.enumName in Exceptions.properties file.
        ServerCommunicationError, //Server communication error.
        ServerAuthenticationError, //Server not valid.
        ServerUserAuthenticationError, //User credentials not valid
        RestInterfaceError, //Interface threw error.
    }
    private RestErrorType type;

    public DwoRestException(RestErrorType type, String message) {
        super(message);
    }

    public String getLocalizedTypeMessage(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Exceptions", locale);
        String msg = TextMapper.getText(localeLookup.getString("DwoRestException" + type.name())); 
        return msg;
    }

    @Override
    public String getMessage() {
        return super.getMessage(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the type
     */
    public RestErrorType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(RestErrorType type) {
        this.type = type;
    }

}
