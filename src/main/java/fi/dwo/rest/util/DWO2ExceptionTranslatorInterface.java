/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.util;

import fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
public interface DWO2ExceptionTranslatorInterface {
    public String encodeJSON(Dwo2ExceptionCode code, String message);
    public String decodeMessageInJSON(String json);
    public Dwo2ExceptionCode decodeCodeInJSON(String json);
}
