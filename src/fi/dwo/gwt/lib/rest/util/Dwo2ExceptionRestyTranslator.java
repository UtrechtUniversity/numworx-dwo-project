/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.util;

import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class Dwo2ExceptionRestyTranslator implements DWO2ExceptionTranslatorInterface {
    private static final Logger LOG = Logger.getLogger(Dwo2ExceptionRestyTranslator.class.getName());

    public Dwo2ExceptionRestyTranslator() {
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

    @Override
    public String getLocalizedCodeExplanation(Locale locale, Dwo2ExceptionCode code) {
        String msg;
        try {
            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
            ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
            msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message.", e);
            msg = "Internal error reading Dwo2Exception locale properties.";
        }
        return msg;
    }    
}
