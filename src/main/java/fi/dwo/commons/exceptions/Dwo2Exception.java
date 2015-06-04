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
    public Dwo2ExceptionCode getDwo2Code() {
        return code;
    }

    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
        String msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName()+"."+code.name());
        return msg;
    }

    @Override
    public String getDwo2Message() {
        return super.getMessage();
    }

}
