package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsEditStudentView implements EditStudentPresenter.Display{

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(String role) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStudent(DomUserFull student) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEmptyTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoadingTableMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
