package nl.uu.fi.dwo.account.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;

public class Account implements EntryPoint {

	Logger LOG = Logger.getLogger("Account");
	
	@Override
	public void onModuleLoad() {
	
		LOG.info("started");
		HeaderPanel header = new HeaderPanel();
		RootPanel.get().add(header);
		
		header.setCenter("Account");
		UserBar user = new UserBar();
		Map<String, Object> profile = new HashMap<String,Object>();
		profile.put("firstname", "Gert");
		profile.put("middlename", "van der");
		profile.put("lastname", "Plas");
		profile.put("userID", 12345);
		profile.put("username", "project_plas");
		user.setProfile(profile);
		header.setRightWidget(user);
	}

}
