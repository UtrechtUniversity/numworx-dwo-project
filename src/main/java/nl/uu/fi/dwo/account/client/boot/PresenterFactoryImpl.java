package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsPresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class PresenterFactoryImpl implements PresenterFactory {

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final MainPresenter mainPresenter;
    private final LoginPresenter loginPresenter;
    private final ResultsPresenter resultsPresenter;
    private final SwitchSchoolPresenter switchSchoolPresenter;

    public PresenterFactoryImpl(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
        eventBus = anEventBus;
        mainPresenter = new MainPresenter(eventBus, dwoGlobalVars);
        loginPresenter = new LoginPresenter(eventBus, dwoGlobalVars);
        resultsPresenter = new ResultsPresenter(eventBus, dwoGlobalVars);
        switchSchoolPresenter = new SwitchSchoolPresenter(eventBus, dwoGlobalVars);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * @return the dwoGlobalVars
     */
    public DwoGlobalVars getDwoGlobalVars() {
        return dwoGlobalVars;
    }

    /**
     * @return the mainPresenter
     */
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    /**
     * @return the loginPresenter
     */
    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    /**
     * @return the resultsPresenter
     */
    public ResultsPresenter getResultsPresenter() {
        return resultsPresenter;
    }

    /**
     * @return the switchSchoolPresenter
     */
    public SwitchSchoolPresenter getSwitchSchoolPresenter() {
        return switchSchoolPresenter;
    }

}
