package fi.dwo.commons.exceptions;

import com.owlike.genson.Genson;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dwo2 Rest exception. This exception contains an error code and message. The
 * error code can be used to lookup general description that is localized for 
 * translation. Primary use is for the client-side. The message part is not 
 * localized as it is assumed to contain stack traces and other internal code 
 * info which by default is English. For GUI messaging it is recommended to use 
 * the method {@Link getLocalizedTypeMessage getLocalizedTypeMessage}.
 *
 * @author G.A.J. van der Plas
 */
public class Dwo2RestException extends WebApplicationException implements Dwo2ExceptionInterface {

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
     * Returns the Dwo2 exception message. The function extracts the component from
     * the JSON string stored in the default exception message.
     * 
     * @return A Dwo2 message string.
     * 
     * @see Dwo2ExceptionCode
     * @return 
     */
    @Override
    public String getDwo2Message(){
        return decodeMessageInJSON(super.getMessage());
    }

    /**
     * Encodes the parameters as a JSON string into the Exception message. The default 
     * status code BAD_REQUEST is set via the Response for the WebApplicationException.
     * 
     * @param code A Dwo2 exception code.
     * @param message A Dwo2 message string.
     * 
     * @see Response
     * @see WebApplicationException
     */
    public Dwo2RestException(Dwo2ExceptionCode code, String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
             .entity(encodeJSON(code,message)).type(MediaType.TEXT_PLAIN).build()
        );
    }

    /**
     * Encodes the code and message parameters as a JSON string into the Exception message.
     * The status code is set via  Response for the WebApplicationException.
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
             .entity(encodeJSON(code,message)).type(MediaType.TEXT_PLAIN).build()
        );
    }

    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
        String msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + getDwo2Code().name());
        return msg;
    }

    private static String encodeJSON(Dwo2ExceptionCode code, String message) {
        Genson genson = new Genson();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Dwo2ExceptionCode", code.name());
        map.put("msg", message);
        String json = genson.serialize(map); 
        return json;
    }
    
    private static String decodeMessageInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        return (String) map.get("msg");
    }

    private static Dwo2ExceptionCode decodeCodeInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        return (Dwo2ExceptionCode) map.get("Dwo2ExceptionCode");
    }    
}
