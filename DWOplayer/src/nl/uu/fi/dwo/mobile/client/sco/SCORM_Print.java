package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public class SCORM_Print extends SCORM_2004_API {
	
	final Logger LOG = Logger.getLogger(getClass().getName());

	static final Map<String, String> VALUES = new HashMap<>();
	static {
		VALUES.put(Memento.LESSON_MODE, "browse");
		VALUES.put(Memento.COMPLETION_STATUS, Memento.COMPLETED);
		VALUES.put(Memento.LOCATION, "0");
	}
	
	public SCORM_Print() {
	}

	@Override
	public Promise<String> Terminate() {
		return super.Terminate();
	}

	@Override
	public String GetValue(String name) {
		String value = VALUES.get(name);
		if (value != null) return value;
		return super.GetValue(name);
	}

	@Override
	public String SetValue(String name, String value) {
		if (Memento.LOCATION.equals(name)) {
			VALUES.put(name, value);
		}
		return "true"; // sink...
	}

	@Override
	public Promise<String> Commit() {
		return Promises.resolved("");
	}

	public static class NoSupport extends Exception { } // marker instead of resolve("")
	
	@Override
	public Promise<String> getValuePromise(String name) {
		LOG.warning("GetValueAsync " + name + " " + hasGetValueAsync() );
		if (hasGetValueAsync()) return super.getValuePromise(name);
		return Promises.resolved(GetValue(name)); // try synchronous
	}

	@Override
	public Promise<Map<String, String>> getValuesPromise(Collection<String> names) {
		LOG.warning("GetValuesAsync " + names);
		return super.getValuesPromise(names);
	}

}
