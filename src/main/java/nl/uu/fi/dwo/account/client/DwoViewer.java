/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Window;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

/**
 * Creates or shows dynamic GUI components.
 * @author G.A.J. van der Plas
 */
public class DwoViewer {
    public static void showMessage(Dwo2ExceptionCode code){
        Window.alert(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.instance().getDwoLocale(), code));
    }
}
