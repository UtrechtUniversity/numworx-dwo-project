package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.ExceptionMapper;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class Dwo2ExceptionMapper extends ExceptionMapper {

	@Override
	public Throwable createNoResponseException() {
		// TODO Auto-generated method stub
		return super.createNoResponseException();
	}

	@Override
	public Throwable createFailedStatusException(Method method,
			Response response) {
		int statuscode = response.getStatusCode();
		if (statuscode == 401) {
			return new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, "relogin");
		}
		String status = response.getStatusText();
		String json = response.getText();
		String type = response.getHeader("Content-Type");
		if ( json != null && json.contains("Dwo2Exception") ) // FIXME 
		{
			try {
				JSONValue value = JSONParser.parseLenient(json);
				JSONObject obj = value.isObject();
				Dwo2ExceptionCode code = Dwo2ExceptionCode.valueOf(obj.get("Dwo2ExceptionCode").isString().stringValue());
				String message = obj.get("msg").isString().stringValue();				
				return new Dwo2Exception(code, message);
			} catch (Exception e) {
			}		
		} else if ( json != null && json.contains("error") && statuscode == 400) {
			try { 
				JSONObject obj = JSONParser.parseStrict(json).isObject();
				String message = obj.get("error").isString().stringValue();
				if ("invalid_grant".equals(message)) {
					return new Dwo2Exception(Dwo2ExceptionCode.Rest_LoginNeeded, message);
				}
			} catch( Exception e) {}	
		}else{
           return new Dwo2Exception(Dwo2ExceptionCode.Rest_CanNotReachServer, status);
        }
		return super.createFailedStatusException(method, response);
	}

}
