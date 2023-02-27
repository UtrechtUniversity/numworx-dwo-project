package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Deferred;

public class RestyDeferred<T> extends Deferred<RestyResponse<T>> implements MethodCallback<T> {

	@Override
	public void onFailure(Method method, Throwable exception) {
		fail(new RestyException(method, exception));
	}

	@Override
	public void onSuccess(Method method, T response) {
		resolve(new RestyResponse<T>(method, response));
	}

}
