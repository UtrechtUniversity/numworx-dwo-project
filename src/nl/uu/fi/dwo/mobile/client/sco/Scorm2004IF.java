package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Scorm2004IF {

	String Commit();

	String GetValue(String name);

	String GetLastError();

	String SetValue(String name, String value);

	String Terminate();
	
	String Initialize();

// Bootstrap method, 
	public void Initialize(final AsyncCallback<Void> callback);

	Role getRole();
}
