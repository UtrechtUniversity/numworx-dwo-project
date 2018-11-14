package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsMethod;
//import jsinterop.annotations.JsType;

/**
 *
 * @author Gert van der Plas
 */
//@JsType(name="DwoPresenterFactory", namespace = JsPackage.GLOBAL)
public class DwoPresenterFactory {

    private static DwoPresenterFactory factory;
    private static PresenterFactory fac;

    
     @JsMethod
    public static DwoPresenterFactory getDwoPresenterFactory() {
        return factory;
    }
    
    public DwoPresenterFactory(PresenterFactory impl) {
        fac = impl;
        factory = this;
    }


    /**
     * @return the fac
     */
    @JsMethod
    public PresenterFactory getFac() {
        return fac;
    }

    /**
     * @param fac the fac to set
     */
    @JsMethod
    public void setFac(PresenterFactory fac) {
        this.fac = fac;
    }
}
