package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @author G.A.J. van der Plas
 */

@JsType(namespace= JsPackage.GLOBAL , name= "jstest")
public class jstest {

    @JsProperty(namespace = JsPackage.GLOBAL, name = "myobject")
    static String value;

    public static jstest createJsTest(){
        return new jstest();
    }
    
    @JsConstructor
    public jstest(){
     value = "test";    
    }
    public static String getString() {
        return value;
    }

    public static void setString(String v) {
        value = v;
    }
}
