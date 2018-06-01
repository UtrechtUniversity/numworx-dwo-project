package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.rest.dom.DomResultTree;

/**
 * Mapper to allow java interface implementation.
 *
 * @author G.A.J. van der Plas
 */
public class JsStudentScoResultView implements StudentScoResultPresenter.Display {

    private static final Logger LOG = Logger.getLogger(JsStudentScoResultView.class.getName());

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setResultTree(DomResultTree data) {
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
