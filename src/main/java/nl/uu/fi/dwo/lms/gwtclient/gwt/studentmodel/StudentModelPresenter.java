package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherStudentModelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;

public class StudentModelPresenter {

    private Display view;

    public interface Display extends BasicDisplay {
    }
    
    @Inject void setView(JsTeacherStudentModelView view) {
    	this.view = view;
    }
    
    @Inject StudentModelPresenter() {
    	
    }
    
    public void init() {
    	view.clear();
    	view.init();
    	
    }
}
