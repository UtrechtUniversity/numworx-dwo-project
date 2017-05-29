package nl.uu.fi.dwo.account.client.boot;

import nl.uu.fi.dwo.account.client.boot.Results.ResultsView;

/**
 * Local ViewFactory implementation class. 
 * 
 * @author G.A.J. van der Plas
 */
public class ViewFactoryImpl implements ViewFactory {
    private final MainView mainView;
    private final LoginView loginView;
    private final ResultsView resultsView;
    private final SwitchSchoolView switchSchoolView;
    
    public ViewFactoryImpl(PresenterFactory pf){
    mainView = new MainView(pf.getMainPresenter());
    loginView = new LoginView(pf.getLoginPresenter());
    resultsView = new ResultsView(pf.getResultsPresenter());
    switchSchoolView = new SwitchSchoolView(pf.getSwitchSchoolPresenter());
        
    }
    @Override
    public MainView getMainView() {
        return mainView;
    }

    @Override
    public LoginView getLoginView() {
        return loginView;
    }

    @Override
    public ResultsView getResultsView() {
        return resultsView;
    }
    @Override
    public SwitchSchoolView getSwitchSchoolView(){
        return switchSchoolView;
    }

}