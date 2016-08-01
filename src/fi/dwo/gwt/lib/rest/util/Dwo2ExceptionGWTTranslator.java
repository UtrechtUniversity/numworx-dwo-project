/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.util;

import fi.dwo.rest.DwoLocale;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;

import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class Dwo2ExceptionGWTTranslator implements DWO2ExceptionTranslatorInterface {
    private static final Logger LOG = Logger.getLogger(Dwo2ExceptionGWTTranslator.class.getName());
    
    public Dwo2ExceptionGWTTranslator() {
    }

    @Override
    public String encodeJSON(Dwo2ExceptionCode code, String message) {
        JSONObject json = new JSONObject();
        json.put("msg", new JSONString(message));
        json.put("Dwo2ExceptionCode", new JSONString(code.name()));
        return json.toString();
    }

    @Override
    public String decodeMessageInJSON(String json) {
		JSONValue value = JSONParser.parseLenient(json);
		JSONObject obj = value.isObject();
		return obj.get("msg").isString().stringValue();
    }

    @Override
    public Dwo2ExceptionCode decodeCodeInJSON(String json) {
		JSONValue value = JSONParser.parseLenient(json);
		JSONObject obj = value.isObject();
		return Dwo2ExceptionCode.valueOf(obj.get("Dwo2ExceptionCode").isString().stringValue());
    }

    @Override
    public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
        String msg;
        msg = code.name();
        try {
            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
            //ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
            //msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
            
            msg = Dwo2ExceptionsForGWT.instance.getString("Dwo2ExceptionCode_"+msg);
        }
        catch (MissingResourceException m) {
            LOG.log(Level.SEVERE, "Can't find Dwo2ExceptionCode_" + code.name(), m);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message. " + code.name(), e);
            msg = "Internal error reading Dwo2Exception locale properties.";
        }
        return msg;
    }    
}
