package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.DwoGlobalVars;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionRestyTranslator;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.util.logging.Level;

public class Account implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    private DomUserFull user = null;
    private SecuredUserAccountManager handler = new SecuredUserAccountManager();
    private LoginStatusPanel loginStatusPanel = new LoginStatusPanel();
    UserBar userBar = new UserBar();

    static {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionRestyTranslator());
    }

    @Override
    public void onModuleLoad() {
        LOG.log(Level.INFO, "onModuleLoad...");
        HeaderPanel header = new HeaderPanel();
        RootPanel.get().add(header);

        header.setCenter("Account");
        if (user == null) {
            LOG.log(Level.INFO, "filling in test user...");
//            DomUserFull curUser;// = new DomUserFull();
//            curUser.setGivenName("Wim");
//            curUser.setInsertion("van");
//            curUser.setFamilyName("Velthoven");
//            curUser.setId(null);
//            curUser.setSingleSchool(false);
//            curUser.setPassword("passw"); //md5Hash = d79096188b670c2f81b7001f73801117
//            curUser.setUserName("project_wim");
//            user = curUser;
            //Try to login and fetch the user
            LOG.log(Level.INFO, "filled in test user.");
            handler.login("project_wim", "passw", new AsyncCallback<DomUserFull>() {
                @Override
                public void onFailure(Throwable t) {
                    loginStatusPanel.setStatus("", false);
                    LOG.log(Level.INFO, t.getStackTrace().toString());
                    Window.alert("Couldn't fetch a testuser from the server.");
                }

                @Override
                public void onSuccess(DomUserFull result) {
                    LOG.log(Level.INFO, "Fetched a test user with username:" + result.getUserName() + ".");
                    user = (DomUserFull) result;
                    loginStatusPanel.setStatus(user.getUserName(), true);
                    DwoGlobalVars.instance().setCurrentUser(user);
                    LOG.log(Level.INFO, "DwoGlobalVars has user with username:" + DwoGlobalVars.instance().getCurrentUser().getDisplayName()+ ".");
                }
            });
        } else {
            LOG.log(Level.INFO, "Configured username for the UserBar is: " + user.getUserName() + ".");
        }
        header.setRightWidget(userBar);
    }

}
