package nl.numworx.async.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.async.Async;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String,?> props = new Hashtable<String,Object>();
		context.registerService(Async.class, new AsyncImpl(context), props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
