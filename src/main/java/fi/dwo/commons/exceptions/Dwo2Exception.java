/*Copyrighted 2015. */
package fi.dwo.commons.exceptions;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * New Dwo2 exception code
 * @author Gert van der Plas
 */
public class Dwo2Exception extends Exception implements Dwo2ExceptionInterface{
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

    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
        String msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName()+"."+code.name());
        return msg;
    }

}
