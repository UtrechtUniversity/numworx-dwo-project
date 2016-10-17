package fi.dwo.gwt.lib.rest.util;

import org.osgi.util.promise.Deferred;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class PromiseCallback<T> extends Deferred<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		fail(caught);
	}

	@Override
	public void onSuccess(T result) {
		resolve(result);
	}

}
