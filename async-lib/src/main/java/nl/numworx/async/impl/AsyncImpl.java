package nl.numworx.async.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.promise.Promise;

import nl.numworx.async.Async;
import nl.numworx.async.AsyncMediator;

public class AsyncImpl extends Async implements org.osgi.service.async.Async {

	final BundleContext context;

	public AsyncImpl(BundleContext context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T mediate(ServiceReference<? extends T> target, Class<T> iface) {
		T runner = context.getService(target);
		ClassLoader loader = iface.getClassLoader(); // ???
		
		InvocationHandler h = new AsyncMediator(this, runner);
		Class<?>[] interfaces = new Class<?>[] { iface };
		return (T) Proxy.newProxyInstance(loader, interfaces, h);
	}

	@Override
	public Promise<Void> execute() {	
		return call().then(p -> null);
	}

}
