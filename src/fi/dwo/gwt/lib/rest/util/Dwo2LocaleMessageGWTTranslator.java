/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.util;

import nl.uu.fi.dwo.rest.DwoLocale;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.DWO2LocaleMessageTranslatorInterface;

/**
 *
 * @author Gert van der Plas
 */
public class Dwo2LocaleMessageGWTTranslator implements DWO2LocaleMessageTranslatorInterface {
    private static final Logger LOG = Logger.getLogger(Dwo2LocaleMessageGWTTranslator.class.getName());
    
    public Dwo2LocaleMessageGWTTranslator() {
    }

    @Override
    public String encodeJSON(Dwo2LocaleMessageCode code, String message) {
        JSONObject json = new JSONObject();
        json.put("msg", new JSONString(message));
        json.put("Dwo2LocaleMessageCode", new JSONString(code.name()));
        return json.toString();
    }

    @Override
    public String decodeMessageInJSON(String json) {
		JSONValue value = JSONParser.parseLenient(json);
		JSONObject obj = value.isObject();
		return obj.get("msg").isString().stringValue();
    }

    @Override
    public Dwo2LocaleMessageCode decodeCodeInJSON(String json) {
		JSONValue value = JSONParser.parseLenient(json);
		JSONObject obj = value.isObject();
		return Dwo2LocaleMessageCode.valueOf(obj.get("Dwo2LocaleMessageCode").isString().stringValue());
    }

    @Override
    public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2LocaleMessageCode code) {
        String msg;
        msg = code.name();
        try {
            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
            //ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
            //msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
            
            msg = DwoLocalesForGWT.instance.getString("Dwo2LocaleMessageCode_"+msg);
        }
        catch (MissingResourceException m) {
            LOG.log(Level.SEVERE, "Can't find Dwo2LocaleMessageCode_" + code.name(), m);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2LocaleMessages.properties, returning English log message. " + code.name(), e);
            msg = "Internal error reading Dwo2LocaleMessage locale properties.";
        }
        return msg;
    }    
}
