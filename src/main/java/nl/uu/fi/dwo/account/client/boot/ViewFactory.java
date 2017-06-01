package nl.uu.fi.dwo.account.client.boot;

import nl.uu.fi.dwo.account.client.boot.Results.ResultsPresenter;

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
}