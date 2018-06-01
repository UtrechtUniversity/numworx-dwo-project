package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons;

import java.util.Map;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author G.A.J. van der Plas
 */
public class JsAddPersonView implements AddPersonPresenter.Display{

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showSchoolClasses(Map<String, DomSchoolClass> schoolClasses) {
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
