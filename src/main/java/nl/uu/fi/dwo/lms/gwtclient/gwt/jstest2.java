package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @author G.A.J. van der Plas
 */

@JsType(namespace= JsPackage.GLOBAL , name= "jstest2")
public class jstest2 {

    String value;

    public static jstest2 createJsTest(){
        return new jstest2();
    }
    
    @JsConstructor
    public jstest2(){
     value = "test";    
    }
    public String getString() {
        return value;
    }

    public void setString(String v) {
        value = v;
    }
}
