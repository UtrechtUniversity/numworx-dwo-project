/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.exceptions;

import fi.dwo.commons.system.TextMapper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.ws.rs.WebApplicationException;

/**
 * Persistence error message. This exception contains an error type that can be
 * localized for translation. The message part is not localized as it is assumed
 * to contain stack traces and other internal code info. For GUI messaging it is
 * recommended to use the method getLocalizedTypeMessage.
 *
 * @author G.A.J. van der Plas
 */
public class Dwo2RestException extends WebApplicationException implements Dwo2ExceptionInterface {

    private Dwo2ExceptionCode code;

    /**
     * @return the code
     */
    @Override
    public Dwo2ExceptionCode getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    @Override
    public void setCode(Dwo2ExceptionCode code) {
        this.code = code;
    }

    public Dwo2RestException(Dwo2ExceptionCode code, String message) {
        super(message);
    }

    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
        String msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName()+"."+code.name());
        return msg;
    }

    @Override
    public String getMessage() {
        return super.getMessage(); //To change body of generated methods, choose Tools | Templates.
    }

}
