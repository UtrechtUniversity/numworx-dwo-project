package nl.uu.fi.dwo.lms.gwtclient.gwt.welcome;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
@RoleScope
public class WelcomePresenter {

    private static final Logger LOG = Logger.getLogger(WelcomePresenter.class.getName());
    private Display view;
    private DwoLocalesForGWT resourceBindings = DwoLocalesForGWT.instance;
    private final Promise<String> welcome;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        this.init();
    }

    /**
     * @return the resourceBindings
     */
    public DwoLocalesForGWT getResourceBindings() {
        return resourceBindings;
    }

    /**
     * @param resourceBindings the resourceBindings to set
     */
    public void setResourceBindings(DwoLocalesForGWT resourceBindings) {
        this.resourceBindings = resourceBindings;
    }

    public interface Display extends BasicDisplay {
        public void setDefaultText();
        public void setWelcomeText(String html);
    }
    
    @Inject WelcomePresenter(EventBus anEventBus, DwoGlobalVars vars, GwtClientMessages rb) {
    	Promise<DomDwoProfileFull> profile = vars.getProfile();
    	profile = profile.filter(v -> v.getDwoProfileRights().contains("c"));    	
        RoleType role = vars.getRole();
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		welcome = 
				profile.flatMap( p ->
				{
					String name = p.getDwoProfileName();
					name = URL.encodePathSegment(name);
					String url = BootPanelController.getBase() + "css/" + name + "_welcome_" + role.name().toLowerCase() + "_" + locale 
							+ ".html";
					return getText(url);
				}						
				).recover(p -> rb.welcomeText());
    }

    private Promise<String> getText(String url) {
    	Deferred<String> defer = new Deferred<String>();
    	RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		rb.setTimeoutMillis(1000000);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{
	
				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					if (responseText.length() > 4 && response.getStatusCode() == 200)
					{
						defer.resolve(responseText);
					} else {						
						defer.fail(new RuntimeException());
					}
				}
	
				@Override
				public void onError(Request request, Throwable exception)
				{
				    defer.fail(exception);
				}
			});
	
		}
		catch (RequestException e)
		{
			defer.fail(e);
		}  	
    	
    	return defer.getPromise();
    }
    
    
    public void init() {
        welcome.then(p -> {
            view.clear();
            view.init();        	
        	view.setWelcomeText(p.getValue()); return p;});
    }

}
