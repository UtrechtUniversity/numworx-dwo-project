package nl.uu.fi.dwo.account.client.boot;

import nl.uu.fi.dwo.account.client.boot.Results.ResultsPresenter;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsView;

/**
 * Local ViewFactory implementation class. 
 * 
 * @author G.A.J. van der Plas
 */
public class ViewFactoryImpl implements ViewFactory {
    private final MainPresenter.Display mainView;
    private final LoginPresenter.Display loginView;
    private final ResultsPresenter.Display resultsView;
    private final SwitchSchoolPresenter.Display switchSchoolView;
    private final ScoResultsPresenter.Display scoResultsView;
    private final SchoolclassesPresenter.Display schoolclassesView;
    private final AccountPresenter.Display accountView;
    
    public ViewFactoryImpl(PresenterFactory pf){
    mainView = new MainView(pf.getMainPresenter());
    loginView = new LoginView(pf.getLoginPresenter());
    resultsView = new ResultsView(pf.getResultsPresenter());
    switchSchoolView = new SwitchSchoolView(pf.getSwitchSchoolPresenter());
    scoResultsView = new ScoResultsView(pf.getScoResultsPresenter());
    schoolclassesView = new SchoolclassesView(pf.getSchoolclassesPresenter());
    accountView = new AccountView(pf.getAccountPresenter());
    }
    
    @Override
    public MainPresenter.Display getMainView() {
        return mainView;
    }

    @Override
    public LoginPresenter.Display getLoginView() {
        return loginView;
    }

    @Override
    public ResultsPresenter.Display getResultsView() {
        return resultsView;
    }
    
    @Override
    public SwitchSchoolPresenter.Display getSwitchSchoolView(){
        return switchSchoolView;
    }

    @Override
    public ScoResultsPresenter.Display getScoResultsView() {
        return scoResultsView;
    }
    
    @Override
    public SchoolclassesPresenter.Display getSchoolclassesView(){
        return schoolclassesView;
    }

    @Override
    public AccountPresenter.Display getAccountView() {
        return accountView;
    }

}