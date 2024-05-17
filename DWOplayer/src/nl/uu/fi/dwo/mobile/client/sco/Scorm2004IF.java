package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.util.function.Function;
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
	default String getRefreshToken() { return null; }
	
	default Promise<String> getValuePromise(String name) {
		return Promises.resolved(GetValue(name));
	}
	
	default Promise<Map<String,String>> getValuesPromise(Collection<String> names) {
		 List<Promise<String>> all = names.stream().map(this::getValuePromise).collect(Collectors.toList());
		 Function<List<String>, Map<String,String>> mapper = list -> {
			 Map<String,String> result = new HashMap<>();
			 Iterator<String> i = names.iterator();
			 for (String v: list) {
				 result.put(i.next(), v);
			 }					 
			 return result;
		 };
		 Promise<List<String>> p = Promises.all(all);
		 return p.map(mapper); 
	}
}
