package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;

import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.json.client.JSONValue;

public interface ScoDataRestCaller {

	public void getValues(RestScormValues restScormValues,
			MethodCallback<DomScormValues> callback);

	public void setValues(RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void setValuesETag(String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void patchValues(String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

	public void getJSONLaunchDataBytes(RestScoContext rest,
			MethodCallback<JSONValue> callback);

}