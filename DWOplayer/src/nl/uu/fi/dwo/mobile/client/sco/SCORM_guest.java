package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_guest implements Scorm2004IF {

	
	protected static final Promise<String> PROMISES = Promises.resolved("");
    private String location;
	
	@Override
	public Promise<String> Commit() {
		return PROMISES;
	}

	@Override
	public String GetValue(String name) {
		if(Memento.LEARNER_ID.equals(name))
			return "guest";
		if(Memento.LEARNER_NAME.equals(name))
			return "Guest, Anonymous";
		if(Memento.LEARNER_PREFERENCE_LANGUAGE.equals(name))
			return 	getLocale();
		if(Memento.LOCATION.equals(name))
			return location;
		return "";
	}

	public Role getRole() {
		return Role.Learner;
	}
	
	
	static String getLocale() {
		String locale;
		locale = LocaleInfo.getCurrentLocale().getLocaleName();
		String query = Window.Location.getQueryString();
		int k = query.indexOf("locale=");
		if(k > 0)
		{
			query = query.substring(k+7);
			k = query.indexOf('&');
			if(k > 0) query = query.substring(0, k);
			locale = query;
		}
		return locale;
	}

	@Override
	public String GetLastError() {
		return "";
	}

	@Override
	public String SetValue(String name, String value) {
		if(Memento.LOCATION.equals(name))
			location = value;
		return "";
	}

	@Override
	public Promise<String> Terminate() {
		return PROMISES;
	}

	@Override
	public Promise<String> Initialize() {
		location = "";
		return PROMISES;
	}

	public void setScoID(String scoID) {
		
	}
	public void setScoID(int scoID) {
	}
	
	public void Initialize(final AsyncCallback<Void> callback) {
		location = "";
		if(callback!=null) callback.onSuccess(null); }
}
