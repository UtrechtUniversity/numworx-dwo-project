package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;

import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.json.client.JSONValue;

public interface ScoDataRestCaller {

	public void getValues(String id, RestScormValues restScormValues,
			MethodCallback<DomScormValues> callback);

	public void setValues(String id, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void setValuesETag(String id, String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void patchValues(String id, String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void getJSONLaunchDataBytes(String id, RestScoContext rest,
			MethodCallback<JSONValue> callback);
	
	default void getJSONLaunchDataBytes( String id,
    		Number scoId,
    		Number profileId,
    		Number classId,
			MethodCallback<JSONValue> callback) {
		callback.onFailure(null, new Error());
	}

}