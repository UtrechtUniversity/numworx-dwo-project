package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 *
 * @author Gert van der Plas
 */
@JsType(name="DwoPresenterFactory")//, namespace = JsPackage.GLOBAL)
public class DwoPresenterFactory {

    private static DwoPresenterFactory factory;
    private PresenterFactoryImpl fac;
    private jstest2 test;

    public static DwoPresenterFactory getDwoPresenterFactory() {
        return factory;
    }
    
    public DwoPresenterFactory(PresenterFactoryImpl impl) {
        fac = impl;
        test = new jstest2();
        factory = this;
    }

    @JsIgnore
    public jstest2 getJsTest() {
        return test;
    }

    /**
     * @return the fac
     */
    @JsMethod
    public PresenterFactoryImpl getFac() {
        return fac;
    }

    /**
     * @param fac the fac to set
     */
    @JsMethod
    public void setFac(PresenterFactoryImpl fac) {
        this.fac = fac;
    }
}
