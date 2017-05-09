package nl.numworx.async;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

class AsyncRunner implements Runnable {

	private final Method method;
	@SuppressWarnings("rawtypes")
	private final Deferred result;
	private final Object[] arguments;
	private final Object object;
	
	public AsyncRunner(Deferred<?> result, Object object, Method method, Object[] arguments) {
		this.result = result;
		this.object = object;
		this.method = method;
		this.arguments = arguments;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			result.resolve(method.invoke(object, arguments));
		} catch(InvocationTargetException t0) {
			result.fail(t0.getTargetException());
		} catch(Throwable t) {
			result.fail(t);
		}
	}

	Promise<?> getPromise() {
		return result.getPromise();
	}
}
