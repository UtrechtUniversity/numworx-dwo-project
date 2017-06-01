package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsPresenter;

/**
 * Client factory interface for GWT app.
 * 
 * @author G.A.J. van der Plas
 */
public interface PresenterFactory {
    public DwoGlobalVars getDwoGlobalVars();
    public EventBus getEventBus();
    public MainPresenter getMainPresenter();
    public LoginPresenter getLoginPresenter();
    public SwitchSchoolPresenter getSwitchSchoolPresenter();    
    public ResultsPresenter getResultsPresenter();
    public ScoResultsPresenter getScoResultsPresenter();
}