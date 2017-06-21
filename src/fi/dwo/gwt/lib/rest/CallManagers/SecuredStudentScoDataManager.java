package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentScoDataRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class SecuredStudentScoDataManager implements StudentScoDataManager {
	
	private SecuredStudentScoDataRestCaller service = GWT.create(SecuredStudentScoDataRestCaller.class);

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#getValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Collection)
	 */
	@Override
	public Promise<Map<String,String>> getValues(DomScoContext sco, DomContext context, Collection<String> keys) {
		PromiseCallback<DomScormValues> defer = new PromiseCallback<DomScormValues>();
		RestScormValues restScormValues = new RestScormValues();
		restScormValues.setRestContext(context);
		DomScormValues values = new DomScormValues();
		values.setScoContext(sco);
		List<DomMapEntry<String, String>> list = new ArrayList<DomMapEntry<String,String>>(keys.size());
		for(String key: keys) {
			DomMapEntry<String,String> entry = new DomMapEntry<String, String>();
			entry.setKey(key);
			entry.setValue("");
			list.add(entry);
		}
		values.setValues(list);
		restScormValues.setDomScormValues(values);
		service.getValues(restScormValues, defer);
		return defer.getPromise().map(new Function<DomScormValues, Map<String,String>>() {

			@Override
			public Map<String, String> apply(
					DomScormValues resolved) {
				HashMap<String,String> result = new HashMap<String,String>();
				for(DomMapEntry<String,String> entry: resolved.getValues()) {
					result.put(entry.getKey(), entry.getValue());
				}
				return result;
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#setValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Map)
	 */
	@Override
	public Promise<?> setValues(DomScoContext sco, DomContext context, Map<String,String> map) {
		RestScormValues rest = new RestScormValues();
		DomScormValues values = new DomScormValues();
		rest.setDomScormValues(values);
		rest.setRestContext(context);
		values.setScoContext(sco);
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			list.add(new DomMapEntry<String,String>(entry));
		}
 		PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
		service.setValues(rest, defer);
		return defer.getPromise();
	}

	@Override
	public Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext id,
			DomDwoProfile value, DomContext context) {
		PromiseCallback<JSONValue> defer = new PromiseCallback<JSONValue>();
		RestScoContext rest = new RestScoContext();
		rest.setDomDwoProfile(value);
		rest.setDomScoContext(id);
		rest.setRestContext(context);
		service.getJSONLaunchDataBytes(rest, defer);
		return defer.getPromise();
	}
}
