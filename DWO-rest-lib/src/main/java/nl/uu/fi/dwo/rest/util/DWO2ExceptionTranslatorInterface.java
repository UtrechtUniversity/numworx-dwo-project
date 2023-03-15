package nl.uu.fi.dwo.rest.util;

import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
public interface DWO2ExceptionTranslatorInterface {
    public String encodeJSON(Dwo2ExceptionCode code, String message);
    public String decodeMessageInJSON(String json);
    public Dwo2ExceptionCode decodeCodeInJSON(String json);
    public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code);
}
