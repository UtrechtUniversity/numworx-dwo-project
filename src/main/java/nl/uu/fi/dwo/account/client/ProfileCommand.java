package nl.uu.fi.dwo.account.client;

import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

public class ProfileCommand implements Command {

	String displayName;
	
	
	@Override
	public void execute() {
		Window.alert("Profile command " + displayName);

	}

	public void setProfile(Map<String, Object> map) {
		displayName =  map.get("firstname") + " " + map.get("middlename") + " " + map.get("lastname");
	}

}
