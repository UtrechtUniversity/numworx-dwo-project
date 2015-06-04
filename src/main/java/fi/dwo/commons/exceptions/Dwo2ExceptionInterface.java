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
    public String getDwo2Message();
    
}
