package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainDisplay;

/**
 *
 * @author G.A.J. van der Plas
 */
@JsType(isNative = true, name="DwoDisplay", namespace = JsPackage.GLOBAL)    
public class DwoDisplay {
    JsMainDisplay jsMainDisplay = new JsMainDisplay();
    public JsMainDisplay getJsMainDisplay(){
        return jsMainDisplay;
    };
}
