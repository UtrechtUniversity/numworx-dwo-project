/*Copyrighted 2015. */
package fi.dwo.rest.exceptions;

import fi.dwo.rest.DwoLocale;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.util.logging.Logger;
import javax.xml.ws.Response;

/**
 * A Dwo2Exception for handling application errors. See
 * {@Link Dwo2ExceptionInterface} or details.
 *
 * @author Gert van der Plas
 */
public class Dwo2Exception extends Exception implements Dwo2ExceptionInterface {

    private static final Logger LOG = Logger.getLogger(Dwo2Exception.class.getName());

    Dwo2ExceptionCode code;
    String message;

    public Dwo2Exception() {
    }

    /**
     *
     * Returns the Dwo2 exception code. The function extracts the component from
     * the JSON string stored in the default exception message.
     *
     * @return A Dwo2 exception code.
     *
     * @see Dwo2ExceptionCode
     */
    @Override
    public Dwo2ExceptionCode getDwo2Code() {
        return Dwo2ExceptionTranslator.decodeCodeInJSON(super.getMessage());
    }

    /**
     *
     * Returns the Dwo2 exception message. The function extracts the component
     * from the JSON string stored in the default exception message.
     *
     * @return A Dwo2 message string.
     *
     * @see Dwo2ExceptionCode
     */
    @Override
    public String getDwo2Message() {
        return Dwo2ExceptionTranslator.decodeMessageInJSON(super.getMessage());
    }

    /**
     * Encodes the parameters as a JSON string into the Exception message. The
     * default status code BAD_REQUEST is set via the Response for the
     * WebApplicationException.
     *
     * @param code A Dwo2 exception code.
     * @param message A Dwo2 message string.
     *
     * @see Response
     */
    public Dwo2Exception(Dwo2ExceptionCode code, String message) {
        super(Dwo2ExceptionTranslator.encodeJSON(code, message));
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getLocalizedCodeExplanation(DwoLocale locale) {
        return Dwo2ExceptionTranslator.getLocalizedCodeExplanation(locale, this.code);
    }

}
