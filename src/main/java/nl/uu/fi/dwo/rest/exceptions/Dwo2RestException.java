package nl.uu.fi.dwo.rest.exceptions;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * A Dwo2 exception for handling rest errors. See 
 * {@Link Dwo2ExceptionInterface} for main details.
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class Dwo2RestException extends WebApplicationException implements Dwo2ExceptionInterface {
    private static final Logger LOG = Logger.getLogger(Dwo2RestException.class.getName());

    
    Dwo2ExceptionCode code;
    String message;
    
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
     * Converts a Dwo2Exception to a Dwo2RestException.
     *
     * @param ex
     * 
     * @see Response
     * @see WebApplicationException
     */
    public Dwo2RestException(Dwo2Exception ex) {
        super(Response.status(400)
                .entity(Dwo2ExceptionTranslator.encodeJSON(ex.getDwo2Code(), ex.getDwo2Message())).type(MediaType.TEXT_HTML).build()
        );
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
     * @see WebApplicationException
     */
    public Dwo2RestException(Dwo2ExceptionCode code, String message) {
        super(Response.status(400)
                .entity(Dwo2ExceptionTranslator.encodeJSON(code, message)).type(MediaType.TEXT_HTML).build()
        );
        this.code = code;
        this.message = message;
    }

    /**
     * Encodes the code and message parameters as a JSON string into the
     * Exception message. The status code is set via Response for the
     * WebApplicationException.
     *
     * @param code A Dwo2 exception code.
     * @param message A Dwo2 message string.
     * @param status A response status code.
     *
     * @see Response
     * @see WebApplicationException
     */
    public Dwo2RestException(Dwo2ExceptionCode code, String message, Response.Status status) {
        super(Response.status(status)
                .entity(Dwo2ExceptionTranslator.encodeJSON(code, message)).type(MediaType.TEXT_PLAIN).build()
        );
    }

        @Override
    public String getLocalizedCodeExplanation(DwoLocale locale) {
        return Dwo2ExceptionTranslator.getLocalizedCodeExplanation(locale, this.code);
    }    
    
//        /**
//     * Returns a localized human readable explanation of the exception code. In 
//     * case the resource can not be read. Return the English log message.
//     * 
//     * @param locale
//     * @return 
//     */
//    @Override
//    public String getLocalizedCodeExplanation(DwoLocale locale) {
//        String msg;
//        try {
//            
//            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
//            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
//            ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", Locale.forLanguageTag(locale.getLocale()));
//            msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
//        }
//        catch (Exception e) {
//            //If resource fails, return the english log message.
//            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message.", e);
//            msg = message;
//        }
//        return msg;
//    }

}
