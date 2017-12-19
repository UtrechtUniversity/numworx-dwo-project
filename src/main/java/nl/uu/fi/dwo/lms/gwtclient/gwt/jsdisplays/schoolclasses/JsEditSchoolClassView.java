package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditSchoolClassView implements EditSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsEditSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsEditSchoolclassDisplay.init();
    }

    @Override
    public void showDialog(String name, Boolean showTree, Boolean hasRegKey, String regKey, boolean edit) {
        JsEditSchoolclassDisplay.showDialog(name, true, true, regKey);
    }

}
