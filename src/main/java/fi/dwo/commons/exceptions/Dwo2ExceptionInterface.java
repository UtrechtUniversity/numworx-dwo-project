/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.exceptions;

import java.util.Locale;

/**
 *
 * @author Gert van der Plas
 */
public interface Dwo2ExceptionInterface {

    /**
     * @return the exception code.
     */
    Dwo2ExceptionCode getCode();

    /**
     * @param code the exception code to set
     */
    void setCode(Dwo2ExceptionCode code);

    /**
     * Returns the localized Code explanation text.
     *
     * @param locale
     * @return 
     */
    public String getLocalizedCodeExplanation(Locale locale);

    /**
     * A full English text error explanation if it exists.
     * @return 
     */
    public String getMessage();
    
}
