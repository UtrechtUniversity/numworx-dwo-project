package nl.uu.fi.dwo.lms.gwtclient.gwt;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 *
 * @author Gert van der Plas
 */
@JsType(name="DwoPresenterFactory")//, namespace = JsPackage.GLOBAL)
public class DwoPresenterFactory {

    private static DwoPresenterFactory factory;
    private PresenterFactoryGwt fac;

    public static DwoPresenterFactory getDwoPresenterFactory() {
        return factory;
    }
    
    public DwoPresenterFactory(PresenterFactoryGwt impl) {
        fac = impl;
        factory = this;
    }


    /**
     * @return the fac
     */
    @JsMethod
    public PresenterFactoryGwt getFac() {
        return fac;
    }

    /**
     * @param fac the fac to set
     */
    @JsMethod
    public void setFac(PresenterFactoryGwt fac) {
        this.fac = fac;
    }
}
