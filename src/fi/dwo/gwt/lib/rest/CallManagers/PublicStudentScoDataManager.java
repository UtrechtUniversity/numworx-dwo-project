package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.GwtRestVars;

public class PublicStudentScoDataManager implements StudentScoDataManager {

	@Override
	public Promise<Map<String, String>> getValues(DomScoContext sco,
			DomContext context, Collection<String> keys) {
		Map<String, String> map = new HashMap<String, String>();
		for(String key: keys) {
			map.put(key, "");
		}
		return Promises.resolved(map);
	}

	@Override
	public Promise<?> setValues(DomScoContext sco, DomContext context,
			Map<String, String> map) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext id,
			DomDwoProfile value, DomContext context) {
		final Deferred<JSONValue> defer = new Deferred<JSONValue>();
		String scoID = id.getId().getIdString();
		int komma = scoID.lastIndexOf(';'); // XXX ons kent ons
		scoID = scoID.substring(komma+1);
		String url = GwtRestVars.getInstance().getServer() + "public/scoData/getJSONLaunchDataBytes?scoId=" + scoID;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		rb.setTimeoutMillis(100000);
		try {
			rb.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == 200) {
						String text = response.getText();
						JSONValue value = JSONParser.parseStrict(text);
						defer.resolve(value);
					} else {
						defer.fail(new RequestException(response.getStatusText()));
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					defer.fail(exception);
				}
			});
		} catch (RequestException e) {
			defer.fail(e);
		}		
		return defer.getPromise();
	}

}
