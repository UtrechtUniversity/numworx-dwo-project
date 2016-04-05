package fi.restrpcgwt.client;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class Callback<T> implements MethodCallback<T> {

	private AsyncCallback<T> callback;
	
	Callback(AsyncCallback<T> callback) {
		this.callback = callback;
	}

	@Override
	public void onFailure(Method method, Throwable exception) {
		callback.onFailure(exception);
	}

	@Override
	public void onSuccess(Method method, T response) {
		callback.onSuccess(response);
	}

}
