package fi.dwo.gwt.lib.rest.CallManagers;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class Callback<T> implements MethodCallback<T> {

	private AsyncCallback<T> callback;
	
	Callback(AsyncCallback<T> callback) {
		this.callback = callback;
	}

	@Override
	public void onFailure(Method method, Throwable exception) {
// TODO move this to a subclass of ExceptionMapper
		Response response = method.getResponse();
		int statuscode = response.getStatusCode();
		String status = response.getStatusText();
		String json = response.getText();
		String type = response.getHeader("Content-Type");
		//if( "application/json".equals(type)) 
		{
			try {
				JSONValue value = JSONParser.parseLenient(json);
				JSONObject obj = value.isObject();
				Dwo2ExceptionCode code = Dwo2ExceptionCode.valueOf(obj.get("Dwo2ExceptionCode").isString().stringValue());
				String message = obj.get("msg").isString().stringValue();				
				exception = new Dwo2Exception(code, message).initCause(exception);
			} catch (Exception e) {
			}		
		}
		callback.onFailure(exception);
	}

	@Override
	public void onSuccess(Method method, T response) {
		callback.onSuccess(response);
	}

}
