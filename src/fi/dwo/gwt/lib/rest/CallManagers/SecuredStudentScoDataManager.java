package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.client.RestCallers.ScoDataRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamScoDataRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentScoDataRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import fi.dwo.gwt.lib.rest.util.RestyDeferred;

public class SecuredStudentScoDataManager implements StudentScoDataManager {
	
	private final ScoDataRestCaller service;

	public SecuredStudentScoDataManager(ScoDataRestCaller caller) {
		service = caller;
	}
	
	public SecuredStudentScoDataManager() {
		this(GWT.<ScoDataRestCaller>create(SecuredStudentScoDataRestCaller.class));
	}
	public SecuredStudentScoDataManager(boolean safe) {
		this(safe 
				? GWT.<ScoDataRestCaller>create(SecuredStudentExamScoDataRestCaller.class)
				: GWT.<ScoDataRestCaller>create(SecuredStudentScoDataRestCaller.class));
	}
	
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#getValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Collection)
	 */
	@Override
	public Promise<Map<String,String>> getValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Collection<String> keys) {
		RestyDeferred<DomScormValues> defer = new RestyDeferred<DomScormValues>();
		RestScormValues restScormValues = new RestScormValues();
		restScormValues.setRestContext(context);
		DomScormValues values = new DomScormValues();
		values.setSchoolClassID(schoolClassID);
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
		return defer.getPromise().
				map(response -> {
					final HashMap<String,String> result = new HashMap<String,String>();
					result.put("ETag", response.method.getResponse().getHeader("etag"));
					for(DomMapEntry<String,String> entry: response.value.getValues()) {
						result.put(entry.getKey(), entry.getValue());
					}
					return result;
				});
		}
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#setValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Map)
	 */
	@Override
	public Promise<?> setValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Map<String,String> map) {
		RestScormValues rest = new RestScormValues();
		DomScormValues values = new DomScormValues();
		rest.setDomScormValues(values);
		rest.setRestContext(context);
		values.setScoContext(sco);
		values.setSchoolClassID(schoolClassID);
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			list.add(new DomMapEntry<String,String>(entry));
		}
		values.setValues(list);
 		RestyDeferred<Boolean> defer = new RestyDeferred<Boolean>();
		service.setValues(rest, defer);
		return defer.getPromise().then(
				p-> {
					if(p.getValue().value.booleanValue()) 
					{
						Promise<String> q = Promises.resolved(p.getValue().method.getResponse().getHeader("etag"));
						return q;
					}
					throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "readonly");
				},
				new Failure() {

					@Override
					public void fail(Promise<?> resolved) throws Exception {
						throw (Exception) resolved.getFailure().getCause();
					}}
				);
	}

	@Override
	public Promise<?> patchValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Map<String,String> map) {
		RestScormValues rest = new RestScormValues();
		DomScormValues values = new DomScormValues();
		rest.setDomScormValues(values);
		rest.setRestContext(context);
		values.setScoContext(sco);
		values.setSchoolClassID(schoolClassID);
		String etag = map.get("ETag");
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			if(! "ETag".equals(entry.getKey()))
				list.add(new DomMapEntry<String,String>(entry));
		}
		values.setValues(list);
 		RestyDeferred<Boolean> defer = new RestyDeferred<Boolean>();
		service.patchValues(etag, rest, defer);
		return defer.getPromise().then(
				p-> {
					if(p.getValue().value.booleanValue()) 
					{
						Promise<String> q = Promises.resolved(p.getValue().method.getResponse().getHeader("etag"));
						return q;
					}
					throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "readonly");
				},
				new Failure() {

					@Override
					public void fail(Promise<?> resolved) throws Exception {
						throw (Exception) resolved.getFailure().getCause();
					}}
				);
	}

	
	
	@Override
	public Promise<JSONValue> getJSONLaunchDataBytes(DomScoContext id,
			DomDwoProfile value, DomSchoolClassId schoolClassID, DomContext context) {
		PromiseCallback<JSONValue> defer = new PromiseCallback<JSONValue>();
		RestScoContext rest = new RestScoContext();
		rest.setDomDwoProfile(value);
		rest.setDomScoContext(id);
		rest.setSchoolClassID(schoolClassID);
		rest.setRestContext(context);
		service.getJSONLaunchDataBytes(rest, defer);
		return defer.getPromise();
	}
}
