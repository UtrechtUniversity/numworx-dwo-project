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
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.util.PathId;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import fi.dwo.gwt.lib.rest.GwtRestVars.TriConsumer;
import fi.dwo.gwt.lib.rest.client.RestCallers.ScoDataRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamScoDataRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentScoDataRestCaller;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import fi.dwo.gwt.lib.rest.util.RestyDeferred;

public class SecuredStudentScoDataManager implements StudentScoDataManager {
	
	private final ScoDataRestCaller service;
	private boolean safe;

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
		this.safe=safe;
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
		F(service::getValues,PathId.getId(context), restScormValues, defer);
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
	
	@Override
	public Promise<Boolean> setValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Map<String,String> map) {
		PromiseCallback<Boolean> defer = new PromiseCallback<>();
		setValuesCommon(sco, schoolClassID, context, map, defer);
		return defer.getPromise();
	}
	
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#setValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Map)
	 */
	@Override
	public Promise<String> setValuesETag(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Map<String,String> map) {
 		RestyDeferred<Boolean> defer = new RestyDeferred<Boolean>();
		setValuesCommon(sco, schoolClassID, context, map, defer);
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

	private void setValuesCommon(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context,
			Map<String, String> map, MethodCallback<Boolean> callback) {
		RestScormValues rest = new RestScormValues();
		DomScormValues values = new DomScormValues();
		rest.setDomScormValues(values);
		rest.setRestContext(context);
		values.setScoContext(sco);
		values.setSchoolClassID(schoolClassID);
		final String etag = map.get("ETag");
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			if(! "ETag".equals(entry.getKey()))
				list.add(new DomMapEntry<String,String>(entry));
		}
		values.setValues(list);
		if(etag == null)
			F(service::setValues,PathId.getId(context), rest, callback);
		else
			F((id, arg, back) -> service.setValuesETag(id, etag, arg, back),PathId.getId(context), rest, callback);
	}

	@Override
	public Promise<?> patchValues(DomScoContext sco, DomSchoolClassId schoolClassID, DomContext context, Map<String,String> map) {
		RestScormValues rest = new RestScormValues();
		DomScormValues values = new DomScormValues();
		rest.setDomScormValues(values);
		rest.setRestContext(context);
		values.setScoContext(sco);
		values.setSchoolClassID(schoolClassID);
		final String etag = map.get("ETag");
		ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
		for(Map.Entry<String, String> entry: map.entrySet()) {
			if(! "ETag".equals(entry.getKey()))
				list.add(new DomMapEntry<String,String>(entry));
		}
		values.setValues(list);
 		RestyDeferred<Boolean> defer = new RestyDeferred<Boolean>();
		TriConsumer<RestScormValues, MethodCallback<Boolean>> triConsumer = (String id, RestScormValues arg, MethodCallback<Boolean> callback) -> service.patchValues(id, etag, arg, callback);
		F( triConsumer,PathId.getId(context), rest, defer);
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
		if (safe || schoolClassID == null) {
			RestScoContext rest = new RestScoContext();
			rest.setDomDwoProfile(value);
			rest.setDomScoContext(id);
			rest.setSchoolClassID(schoolClassID);
			rest.setRestContext(context);
			return F(service::getJSONLaunchDataBytes,PathId.getId(context), rest);
		}
		Number profileId = (Number) PersistenceIdDecoderInterface.instance.idOf(value.getId(), PersistenceClassType.PersistentDwoProfile);
		Number scoId = (Number) PersistenceIdDecoderInterface.instance.idOf(id.getId(), PersistenceClassType.PersistentScoContext);
		Number classId = (Number) PersistenceIdDecoderInterface.instance.idOf(schoolClassID.getId(), PersistenceClassType.PersistentSchoolClass);
		TriConsumer<RestScoContext, MethodCallback<JSONValue>> triConsumer =
				(String sid, RestScoContext rest, MethodCallback<JSONValue> callback) ->
					service.getJSONLaunchDataBytes(sid, scoId, profileId, classId, callback);
		return F(triConsumer, PathId.getId(context), null);
	}
}
