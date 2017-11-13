package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsSchoolClasssesView implements SchoolclassesPresenter.Display{
    @Override
    public void clear() {
        JsSchoolClassesDisplay.clear();
    }

    @Override
    public void init() {
        JsSchoolClassesDisplay.init();
    }

    @Override
    public void updateView(Map<String, SchoolclassesPresenter.ClassItem> data) {
        JsSchoolClassesDisplay.updateView(data);
    }


}
