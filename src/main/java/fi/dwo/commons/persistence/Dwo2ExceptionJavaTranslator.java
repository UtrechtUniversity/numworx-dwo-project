/**
 * Copyrighted Apr 8, 2016
 */
package fi.dwo.commons.persistence;

import com.owlike.genson.Genson;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AutoConfigure for converting to and from JSON from and to Dwo2EXceptions.
 * 
 * @author Gert van der Plas
 */
public class Dwo2ExceptionJavaTranslator implements DWO2ExceptionTranslatorInterface {

    private static final Logger LOG = Logger.getLogger(Dwo2ExceptionJavaTranslator.class.getName());

    public Dwo2ExceptionJavaTranslator() {
    }

    @Override
    public String encodeJSON(Dwo2ExceptionCode code, String message) {
        Genson genson = new Genson();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Dwo2ExceptionCode", code.name());
        map.put("msg", message);
        String json = genson.serialize(map);
        return json;
    }

    @Override
    public String decodeMessageInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        return (String) map.get("msg");
    }

    @Override
    public Dwo2ExceptionCode decodeCodeInJSON(String json) {
        Genson genson = new Genson();
        Map<String, Object> map = (Map<String, Object>) genson.deserialize(json, Map.class);
        String code = (String) map.get("Dwo2ExceptionCode");
//         Dwo2Exception result = genson.deserialize(json, Dwo2Exception.class);
//         Dwo2ExceptionCode code = result.getDwo2Code();
//         return code;
        return Dwo2ExceptionCode.valueOf(code);
    }

    /**
     * Returns a localized human readable explanation of the exception code. In
     * case the resource can not be read. Return the English log message.
     *
     * @param locale
     * @param code
     * @return
     */
    @Override
    public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
        String msg;
        try {
            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
            ResourceBundle localeLookup = ResourceBundle.getBundle("fi.dwo.rest.locale.Dwo2ExceptionMessages", Locale.forLanguageTag(locale.getLocale()));
            msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
//            msg = code.name();
        }
        catch (Exception e) {
            //If resource fails, return the english log message.
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message.", e);
            msg = "Internal error reading Dwo2Exception locale properties.";
        }
        return msg;
    }
}
