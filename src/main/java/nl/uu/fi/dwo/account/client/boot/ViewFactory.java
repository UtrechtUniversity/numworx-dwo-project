package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsView;

/**
 * Client factory interface for GWT app.
 * 
 * @author G.A.J. van der Plas
 */
public interface ViewFactory {
    public MainView getMainView();
    public LoginView getLoginView();
    public ResultsView getResultsView();
    public SwitchSchoolView getSwitchSchoolView();    
}