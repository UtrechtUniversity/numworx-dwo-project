/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.util; 

import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

/**
 * Dwo2ExceptionTranslator must be initialized before usage. A translator object
 * must be set before calling any method.
 *
 *
 * @author Gert van der Plas
 */
public class Dwo2ExceptionTranslator {

    private static final Logger LOG = Logger.getLogger(Dwo2ExceptionTranslator.class.getName());

    static volatile DWO2ExceptionTranslatorInterface translator = null;

    /**
     * ******************GENSON *******************************
     */
    //
    private Dwo2ExceptionTranslator() {
    }

    public Dwo2ExceptionTranslator(DWO2ExceptionTranslatorInterface translator) {
        setTranslator(translator);
    }

    public static synchronized DWO2ExceptionTranslatorInterface setTranslator(DWO2ExceptionTranslatorInterface translator) {
        Dwo2ExceptionTranslator.translator = translator;
        return translator;
    }

    public static String encodeJSON(Dwo2ExceptionCode code, String message) {
        if (translator == null) {
            throw new RuntimeException("Dwo2ExceptionTranlator must be initialized with a translator.");
        }
        return translator.encodeJSON(code, message);
    }

    public static String decodeMessageInJSON(String json) {
        if (translator == null) {
            throw new RuntimeException("Dwo2ExceptionTranlator must be initialized with a translator.");
        }
        return translator.decodeMessageInJSON(json);
    }

    public static Dwo2ExceptionCode decodeCodeInJSON(String json) {
        if (translator == null) {
            throw new RuntimeException("Dwo2ExceptionTranlator must be initialized with a translator.");
        }
        return translator.decodeCodeInJSON(json);
    }
}
