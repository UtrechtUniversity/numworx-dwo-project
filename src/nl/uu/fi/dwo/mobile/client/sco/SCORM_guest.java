package nl.uu.fi.dwo.mobile.client.sco;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_guest implements Scorm2004IF {

	@Override
	public String Commit() {
		return "";
	}

	@Override
	public String GetValue(String name) {
		if(Memento.LEARNER_ID.equals(name))
			return "guest";
		if(Memento.LEARNER_NAME.equals(name))
			return "Guest, Anonymous";
		if(Memento.LEARNER_PREFERENCE_LANGUAGE.equals(name))
			return "nl"; // FIXME uit ?locale=xx halen
		return "";
	}

	@Override
	public String GetLastError() {
		return "";
	}

	@Override
	public String SetValue(String name, String value) {
		return "";
	}

	@Override
	public String Terminate() {
		return "";
	}

	@Override
	public String Initialize() {
		return "";
	}

	public void setScoID(int scoID) {
	}
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }
}
