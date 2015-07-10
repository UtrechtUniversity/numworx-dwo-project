package fi.dwo.commons.exceptions;

import com.owlike.genson.Genson;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

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
        return decodeCodeInJSON(super.getMessage());
    }

    /**
     *
     * Returns the Dwo2 exception message. The function extracts the component
     * from the JSON string stored in the default exception message.
     *
     * @return A Dwo2 message string.
     *
     * @see Dwo2ExceptionCode
     * @return
     */
    @Override
    public String getDwo2Message() {
        return decodeMessageInJSON(super.getMessage());
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
                .entity(encodeJSON(code, message)).type(MediaType.TEXT_HTML).build()
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
                .entity(encodeJSON(code, message)).type(MediaType.TEXT_PLAIN).build()
        );
    }

    /**
     * Returns a localized human readable explanation of the exception code. In 
     * case the resource can not be read. Return the English log message.
     * 
     * @param locale
     * @return 
     */
    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        String msg;
        try {
            //TODO test locale resource finding.
            ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
            msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
        }
        catch (Exception e) {
            //If resource fails, return the english log message.
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message.", e);
            msg = message;
        }
        return msg;
    }

    public static String encodeJSON(Dwo2ExceptionCode code, String message) {
        Genson genson = new Genson();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Dwo2ExceptionCode", code.name());
        map.put("msg", message);
        String json = genson.serialize(map);
        return json;
    }

    public static String decodeMessageInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        return (String) map.get("msg");
    }

    public static Dwo2ExceptionCode decodeCodeInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        String code = (String) map.get("Dwo2ExceptionCode");
        return Dwo2ExceptionCode.valueOf(code);
    }
}
