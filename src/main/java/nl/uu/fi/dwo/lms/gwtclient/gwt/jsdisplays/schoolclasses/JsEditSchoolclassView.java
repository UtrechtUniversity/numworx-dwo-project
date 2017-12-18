package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditSchoolclassView implements EditSchoolclassPresenter.Display{
    @Override
    public void clear() {
        JsAddSchoolclassDisplay.clear();
    }

    @Override
    public void init() {
        JsAddSchoolclassDisplay.init();
    }

    @Override
    public void showDialog(String name, Boolean showTree, Boolean hasRegKey, String regKey, boolean edit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
