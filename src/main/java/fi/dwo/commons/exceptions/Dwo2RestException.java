package fi.dwo.commons.exceptions;

import com.owlike.genson.Genson;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Persistence error message. This exception contains an error type that can be
 * localized for translation. The message part is not localized as it is assumed
 * to contain stack traces and other internal code info. For GUI messaging it is
 * recommended to use the method getLocalizedTypeMessage.
 *
 * @author G.A.J. van der Plas
 */
public class Dwo2RestException extends WebApplicationException implements Dwo2ExceptionInterface {

    //private Dwo2ExceptionCode dwo2Code;

    /**
     * @return the code
     */
    @Override
    public Dwo2ExceptionCode getDwo2Code() {
        return decodeCodeInJSON(super.getMessage());
    }

    public String getDwo2Message(){
        return decodeMessageInJSON(super.getMessage());
    }

    public Dwo2RestException(String message) {
        super(message);
    }
    
    public Dwo2RestException(Dwo2ExceptionCode code, String message) {
        super(encodeJSON(code,message));
    }

        public Dwo2RestException(Dwo2ExceptionCode code, String message, Response.Status status) {
        super(encodeJSON(code,message), status);
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
