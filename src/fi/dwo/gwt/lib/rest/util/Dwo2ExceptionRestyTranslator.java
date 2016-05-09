/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class Dwo2ExceptionRestyTranslator implements DWO2ExceptionTranslatorInterface {
    private static final Logger LOG = Logger.getLogger(Dwo2ExceptionRestyTranslator.class.getName());
    
    Dwo2ExceptionConverter converter = GWT.create(Dwo2ExceptionConverter.class);

    public Dwo2ExceptionRestyTranslator() {
    }

    public String encodeJSON(Dwo2ExceptionCode code, String message) {
        Dwo2Exception exception = new Dwo2Exception(code, message);
        JSONValue json = converter.encode(exception);
        return json.toString();
    }

    public String decodeMessageInJSON(String json) {
        Dwo2Exception exception = converter.decode(json);
        return exception.getDwo2Message();
    }

    public Dwo2ExceptionCode decodeCodeInJSON(String json) {
        Dwo2Exception exception = converter.decode(json);
        return exception.getDwo2Code();
    }

    @Override
    public String getLocalizedCodeExplanation(Locale locale, Dwo2ExceptionCode code) {
        String msg;
        try {
            //Current resources are in /java/resources, however if in java/resources/fi/dwo then
            //replace getBundle("Dwo2Exceptions", locale); with getBundle("fi.dwo.Dwo2Exceptions", locale);
            //ResourceBundle localeLookup = ResourceBundle.getBundle("Dwo2Exceptions", locale);
            //msg = localeLookup.getString(Dwo2ExceptionCode.class.getSimpleName() + "." + code.name());
            msg = "";
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't find the resource Dwo2Exceptions.properties, returning English log message.", e);
            msg = "Internal error reading Dwo2Exception locale properties.";
        }
        return msg;
    }    
}
