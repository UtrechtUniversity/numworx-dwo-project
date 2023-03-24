package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import com.google.gwt.core.client.GWT;
import java.util.ArrayList;
import java.util.List;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Translator access for javascript via jsinteropt For messages not Exceptions.
 *
 * @author Gert van der Plas
 */
@JsType(name = "jsDwoMessageTranslator", namespace="dwoAPI")
public class DwoMessageTranslator {

    private static final DwoLocalesForGWT rb = GWT.create(DwoLocalesForGWT.class);

    /**
     * A comfort function to yield an overview of all available translations for
     * the HTML5 developer.
     *
     * @return
     */
    @JsMethod
    public List<String> getTranslationList() {
        // For Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        List<String> result = new ArrayList<>();
        Dwo2LocaleMessageCode codeList[] = Dwo2LocaleMessageCode.values();
        for (Dwo2LocaleMessageCode code : codeList) {
            result.add(code.name());
        }
        return result;
    }

    /**
     * translates a string to the local encoding set in the application.
     *
     * @param text
     * @return
     */
    @JsMethod
    public String translate(String text) {
        try {
            return rb.getString(text);
        } catch (Exception e) {
            return text;
        }
    }

}
