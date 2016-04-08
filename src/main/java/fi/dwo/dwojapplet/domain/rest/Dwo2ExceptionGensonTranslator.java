/**
 * Copyrighted Apr 8, 2016
 */
package fi.dwo.dwojapplet.domain.rest;

import com.owlike.genson.Genson;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gert van der Plas
 */
public class Dwo2ExceptionGensonTranslator implements DWO2ExceptionTranslatorInterface {

    public Dwo2ExceptionGensonTranslator() {
    }

    public String encodeJSON(Dwo2ExceptionCode code, String message) {
        Genson genson = new Genson();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Dwo2ExceptionCode", code.name());
        map.put("msg", message);
        String json = genson.serialize(map);
        return json;
    }

    public String decodeMessageInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        return (String) map.get("msg");
    }

    public Dwo2ExceptionCode decodeCodeInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        String code = (String) map.get("Dwo2ExceptionCode");
        return Dwo2ExceptionCode.valueOf(code);
    }

}
