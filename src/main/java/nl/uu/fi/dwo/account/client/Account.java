package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Level;

public class Account implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    DomUserFull user = null;
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    private SecuredUserAccountManager handler = new SecuredUserAccountManager();

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
                    LOG.log(Level.INFO, t.getStackTrace().toString());
                    Window.alert("Couldn't fetch a testuser from the server.");
                }

                @Override
                public void onSuccess(DomUserFull result) {
                    LOG.log(Level.INFO, "Fetched a test user with username:" + result.getUserName() + ".");
                    user = (DomUserFull) result;
                    loginStatusPanel.setUser(user);
                }
            });
        } else {
            LOG.log(Level.INFO, "Configured username for the UserBar is: " + user.getUserName() + ".");
        }
        UserBar userBar = new UserBar(user);
        header.setRightWidget(userBar);
    }

}
