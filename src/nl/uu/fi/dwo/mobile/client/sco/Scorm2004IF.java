package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Scorm2004IF {

	Promise<String> Commit();

	String GetValue(String name);

	String GetLastError();

	String SetValue(String name, String value);

	Promise<String> Terminate();
	
	Promise<String> Initialize();

// Bootstrap method, 
	public void Initialize(final AsyncCallback<Void> callback);

	Role getRole();

	void setScoID(String unitId);

	default String getAuthorization() { return "None"; }
	
	default Promise<String> getValuePromise(String name) {
		return Promises.resolved(GetValue(name));
	}
}
