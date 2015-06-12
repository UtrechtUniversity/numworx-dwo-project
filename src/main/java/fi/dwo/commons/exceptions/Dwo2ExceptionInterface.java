/*Copyrighted 2015. */
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
    Dwo2ExceptionCode getDwo2Code();

    /**
     * 
     * Returns the Dwo2 exception message. The function extracts the component from
     * the JSON string stored in the default exception message.
     * 
     * @return A Dwo2 message string.
     * 
     * @see Dwo2ExceptionCode
     * @return 
     */
    public String getDwo2Message();
    
    /**
     * Returns the localized Code explanation text.
     *
     * @param locale
     * @return 
     */
    public String getLocalizedCodeExplanation(Locale locale);

    
}
