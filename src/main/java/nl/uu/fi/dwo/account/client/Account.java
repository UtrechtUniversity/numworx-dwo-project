package nl.uu.fi.dwo.account.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import fi.dwo.rest.dom.entities.DomUserFull;

public class Account implements EntryPoint {

	Logger LOG = Logger.getLogger("Account");
	
	@Override
	public void onModuleLoad() {
	
		LOG.info("started");
		HeaderPanel header = new HeaderPanel();
		RootPanel.get().add(header);
		
		header.setCenter("Account");
		UserBar user = new UserBar();
                DomUserFull curUser = new DomUserFull();
		curUser.setGivenName("Gert");
                curUser.setInsertion("van der");
                curUser.setFamilyName("Plas");
		curUser.setId(null);
		curUser.setSingleSchool(false);
                
		header.setRightWidget(user);
	}

}
