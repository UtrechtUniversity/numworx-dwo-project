/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.rest.util; 

import nl.uu.fi.dwo.rest.DwoLocale;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;

/**
 * Dwo2LocaleMessageTranslator must be initialized before usage. A translator object
 * must be set before calling any method.
 *
 *
 * @author Gert van der Plas
 */
public class Dwo2LocaleMessageTranslator {

    private static final Logger LOG = Logger.getLogger(Dwo2LocaleMessageTranslator.class.getName());

    static volatile DWO2LocaleMessageTranslatorInterface translator = null;

    /**
     * ******************GENSON *******************************
     */
    //
    private Dwo2LocaleMessageTranslator() {
    }

    public Dwo2LocaleMessageTranslator(DWO2LocaleMessageTranslatorInterface translator) {
        setTranslator(translator);
    }

    public static synchronized DWO2LocaleMessageTranslatorInterface setTranslator(DWO2LocaleMessageTranslatorInterface translator) {
        Dwo2LocaleMessageTranslator.translator = translator;
        return translator;
    }

    public static String encodeJSON(Dwo2LocaleMessageCode code, String message) {
        if (translator == null) {
            throw new RuntimeException("Dwo2LocaleMessageTranslator must be initialized with a translator.");
        }
        return translator.encodeJSON(code, message);
    }

    public static String decodeMessageInJSON(String json) {
        if (translator == null) {
            throw new RuntimeException("Dwo2LocaleMessageTranslator must be initialized with a translator.");
        }
        return translator.decodeMessageInJSON(json);
    }

    public static Dwo2LocaleMessageCode decodeCodeInJSON(String json) {
        if (translator == null) {
            throw new RuntimeException("Dwo2LocaleMessageTranslator must be initialized with a translator.");
        }
        return translator.decodeCodeInJSON(json);
    }
    
    public static String getLocalizedCodeExplanation(DwoLocale locale, Dwo2LocaleMessageCode code) {
        if (translator == null) {
            throw new RuntimeException("Dwo2LocaleMessageTranslator must be initialized with a translator.");
        }
        return translator.getLocalizedCodeExplanation(locale, code);
    }
}
