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

    private Dwo2ExceptionCode code;

    /**
     * @return the code
     */
    @Override
    public Dwo2ExceptionCode getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    @Override
    public void setCode(Dwo2ExceptionCode code) {
        this.code = code;
    }

    public Dwo2RestException(Dwo2ExceptionCode code, String message) {
        super(message);
        setCode(code);
    }

        public Dwo2RestException(Dwo2ExceptionCode code, String message, Response.Status status) {
        super(message, status);
        setCode(code);
    }

    @Override
    public String getLocalizedCodeExplanation(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
        String msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
        return msg;
    }

    @Override
    public String getMessage() {
        return encodeJSON();
    }
    
    public String getDwo2Message() {
        return super.getMessage();
    }

    private String encodeJSON() {
        Genson genson = new Genson();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Dwo2ExceptionCode", code.name());
        map.put("msg", super.getMessage());
        String json = genson.serialize(map); 
        return json;
    }
    
    public static Dwo2RestException decodeJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        Dwo2RestException r = new Dwo2RestException((Dwo2ExceptionCode) map.get("Dwo2ExceptionCode"), (String) map.get("msg"));        
        return r;
    }

}
