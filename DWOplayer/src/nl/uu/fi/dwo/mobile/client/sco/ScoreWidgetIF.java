package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Collection;
import java.util.Map;

import org.osgi.util.promise.Promise;

public interface ScoreWidgetIF {

	String SetValue(String name, String value);
	String GetValue(String name);
	Promise<String> Commit();
	Promise<Map<String,String>> getValuesPromise(Collection<String> names);
}
