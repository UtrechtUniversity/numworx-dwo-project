package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsType;

/**
 *
 * @author G.A.J. van der Plas
 */
@JsType(namespace = "myapp")
public class jstest {

    String value;


    public jstest(String aValue){
        this.value = aValue;
    }
    
    public String getString() {
        return value;
    }

    public void setString(String v) {
        value = v;
    }
}
