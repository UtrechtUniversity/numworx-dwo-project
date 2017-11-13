package nl.uu.fi.dwo.lms.gwtclient.gwt.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * GWT Panel that handles the login-authentication.
 *
 * @author G.A.J. van der Plas
 */
public class WelcomeView extends Composite implements WelcomePresenter.Display {

    private static final Logger LOG = Logger.getLogger(WelcomeView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, WelcomeView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private WelcomePresenter welcomePresenter;

    @UiField
    HTMLPanel welcomePanel;
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;


    public WelcomeView(WelcomePresenter wp){
        initWidget(uiBinder.createAndBindUi(this));
        welcomePresenter = wp;
        welcomePresenter.setView(this);                
    }

}
