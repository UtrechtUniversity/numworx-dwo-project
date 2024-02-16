package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import jsinterop.annotations.JsMethod;
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
    private static final List<ConstantsWithLookup> override = new LinkedList<>();

    public static void reset() {
    	override.clear();
    	add(rb);
    }

    static {
    	reset();
    }

    public static void add(ConstantsWithLookup extra) {
    	if (!override.contains(extra)) {
    		override.add(0, extra);
    	}
    }
    public static void remove(ConstantsWithLookup extra) {
    	override.remove(extra);
    }
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
    public String translate(String key) {
    	for (ConstantsWithLookup lookup: override) {
    		try {
    			return lookup.getString(key);
    		} catch(MissingResourceException always) {
    			// always ignore
    		}
    	}
    	return key;
    }
    
}
