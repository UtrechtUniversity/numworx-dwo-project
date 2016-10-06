/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.exceptions;

import nl.uu.fi.dwo.rest.DwoLocale;

/**
 * DWO2 exception interface. An unilateral interface for different base-type Exceptions.
 * The default Exception message now stores a JSON string
 * that contains an exception code and an exception message in English.
 * The {@Link Dwo2ExceptionCode} allows for localized standard messages while the exception
 * details are stored in English in the {@Link Dwo2ExceptionMessage}.
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
     */
    public String getDwo2Message();
    
    /**
     * Returns the localized Code explanation text.
     *
     * @param locale
     * @return 
     */
    public String getLocalizedCodeExplanation(DwoLocale locale);

    
}
