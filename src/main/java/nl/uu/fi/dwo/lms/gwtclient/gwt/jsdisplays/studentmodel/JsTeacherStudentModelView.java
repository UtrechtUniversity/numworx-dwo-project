package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherStudentModelView implements StudentModelPresenter.Display {
    @Override
    public void clear() {
    	JsTeacherStudentModelDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
    	JsTeacherStudentModelDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
    	JsTeacherStudentModelDisplay.init();
    }

    @Inject JsTeacherStudentModelView() {}
}
