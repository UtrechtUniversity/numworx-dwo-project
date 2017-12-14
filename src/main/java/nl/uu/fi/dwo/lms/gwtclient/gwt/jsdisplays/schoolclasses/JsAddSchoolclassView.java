package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddSchoolclassView implements AddSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsAddSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsAddSchoolclassDisplay.init();
    }

}
