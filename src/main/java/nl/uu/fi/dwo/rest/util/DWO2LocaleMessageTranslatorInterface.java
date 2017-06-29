package nl.uu.fi.dwo.rest.util;

import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;

/**
 *
 * @author Gert van der Plas
 */
public interface DWO2LocaleMessageTranslatorInterface {
    public String encodeJSON(Dwo2LocaleMessageCode code, String message);
    public String decodeMessageInJSON(String json);
    public Dwo2LocaleMessageCode decodeCodeInJSON(String json);
    public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2LocaleMessageCode code);
}
