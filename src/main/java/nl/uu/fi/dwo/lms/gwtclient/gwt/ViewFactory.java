package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;

/**
 * Client factory interface for GWT app.
 * 
 * @author G.A.J. van der Plas
 */
public interface ViewFactory {
    public MainPresenter.Display getMainView();
    public LoginPresenter.Display getLoginView();
    public ResultsPresenter.Display getResultsView();
    public SwitchSchoolPresenter.Display getSwitchSchoolView();    
    public ScoResultsPresenter.Display getScoResultsView();
    public SchoolclassesPresenter.Display getSchoolclassesView();
    public AccountPresenter.Display getAccountView();
}