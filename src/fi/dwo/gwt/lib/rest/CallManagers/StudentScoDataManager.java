package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;

import com.google.gwt.json.client.JSONValue;

public interface StudentScoDataManager {

	public abstract Promise<Map<String, String>> getValues(DomScoContext sco,
			DomContext domContext, Collection<String> keys);

	public abstract Promise<?> setValues(DomScoContext sco, DomContext domContext,
			Map<String, String> map);

	Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext id, DomDwoProfile value, DomContext context);
}