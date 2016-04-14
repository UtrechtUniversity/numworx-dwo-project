package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import fi.dwo.rest.dom.entities.DomUserFull;

public class Account implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    DomUserFull user = null;

    @Override
    public void onModuleLoad() {

        LOG.info("started");
        HeaderPanel header = new HeaderPanel();
        RootPanel.get().add(header);

        header.setCenter("Account");
        if (user == null) {
            DomUserFull curUser = new DomUserFull();
            curUser.setGivenName("Wim");
            curUser.setInsertion("van");
            curUser.setFamilyName("Velthoven");
            curUser.setId(null);
            curUser.setSingleSchool(false);
            curUser.setPassword("passw");
            curUser.setUserName("project_wim");
            user=curUser;
        }
        UserBar userBar = new UserBar(user);

        header.setRightWidget(userBar);
    }

}
