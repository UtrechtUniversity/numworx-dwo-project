package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Deferred;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class PromiseCallback<T> extends Deferred<T> implements AsyncCallback<T>, MethodCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		fail(caught);
	}

	@Override
	public void onSuccess(T result) {
		resolve(result);
	}

	@Override
	public void onFailure(Method method, Throwable exception) {
		fail(exception);
	}

	@Override
	public void onSuccess(Method method, T response) {
		resolve(response);
	}

}
