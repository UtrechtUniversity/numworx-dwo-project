package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherScormValuesRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class SecuredTeacherScormValuesManager {
	
	private SecuredTeacherScormValuesRestCaller service = GWT.create(SecuredTeacherScormValuesRestCaller.class);

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#getValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Collection)
	 */
	public Promise<Map<String,String>> getValues(DomStudentScoContext sco, DomContext context, Collection<String> keys) {
		PromiseCallback<DomTeacherScormValues> defer = new PromiseCallback<DomTeacherScormValues>();
		RestTeacherScormValues restScormValues = new RestTeacherScormValues();
		restScormValues.setRestContext(context);
		DomTeacherScormValues values = new DomTeacherScormValues();
		values.setStudentScoContext(sco);
		List<DomMapEntry<String, String>> list = new ArrayList<DomMapEntry<String,String>>(keys.size());
		for(String key: keys) {
			DomMapEntry<String,String> entry = new DomMapEntry<String, String>();
			entry.setKey(key);
			entry.setValue("");
			list.add(entry);
		}
		values.setValues(list);
		restScormValues.setDomTeacherScormValues(values);
		service.get(restScormValues, defer);
		return defer.getPromise().map(new Function<DomTeacherScormValues, Map<String,String>>() {

			@Override
			public Map<String, String> apply(
					DomTeacherScormValues resolved) {
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
	public Promise<DomStudentScoContext> setValues(DomStudentScoContext sco, DomContext context, Map<String,String> map) {
		RestTeacherScormValues rest = new RestTeacherScormValues();
		DomTeacherScormValues values = new DomTeacherScormValues();
		rest.setDomTeacherScormValues(values);
		rest.setRestContext(context);
		values.setStudentScoContext(sco);
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			list.add(new DomMapEntry<String,String>(entry));
		}
		values.setValues(list);
 		PromiseCallback<DomStudentScoContext> defer = new PromiseCallback<DomStudentScoContext>();
		service.set(rest, defer);
		return defer.getPromise();
	}

}
