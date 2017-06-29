/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.locale;

import nl.uu.fi.dwo.rest.DwoLocale;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;

/**
 * A Dwo2LocaleMessage for handling Translations. See
 * {@Link Dwo2LocaleMessageInterface} or details.
 *
 * @author Gert van der Plas
 */
public class Dwo2LocaleMessage implements Dwo2LocaleMessageInterface {

    private static final Logger LOG = Logger.getLogger(Dwo2LocaleMessage.class.getName());

    String message;

    public Dwo2LocaleMessage() {
    }

    /**
     *
     * Returns the Dwo2 exception code. The function extracts the component from
     * the JSON string stored in the default exception message.
     *
     * @return A Dwo2LocaleMessageCode.
     *
     * @see Dwo2LocaleMessageCode
     */
    @Override
    public Dwo2LocaleMessageCode getDwo2Code() {
        return Dwo2LocaleMessageTranslator.decodeCodeInJSON(message);
    }

    /**
     *
     * Returns the Dwo2 exception message. The function extracts the component
     * from the JSON string stored in the default exception message.
     *
     * @return
     *
     * @see Dwo2LocaleMessageCode
     */
    @Override
    public String getDwo2Message() {
        return Dwo2LocaleMessageTranslator.decodeMessageInJSON(message);
    }

    /**
     * Encodes the parameters as a JSON string into the Exception message. The
     * default status code BAD_REQUEST is set via the Response for the
     * WebApplicationException.
     *
     * @param code
     * @param message
     *
     * @see Response
     */
    public Dwo2LocaleMessage(Dwo2LocaleMessageCode code, String aMessage) {
        message = Dwo2LocaleMessageTranslator.encodeJSON(code, aMessage);
    }

    @Override
    public String getLocalizedCodeExplanation(DwoLocale locale) {
        return Dwo2LocaleMessageTranslator.getLocalizedCodeExplanation(locale, Dwo2LocaleMessageTranslator.decodeCodeInJSON(message));
    }

}
