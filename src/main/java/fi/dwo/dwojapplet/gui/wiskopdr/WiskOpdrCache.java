package fi.dwo.dwojapplet.gui.wiskopdr;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import fi.beans.loader.Loader;

public class WiskOpdrCache {

	public static final String WISKOPDR = "fi.wiskopdr.WiskOpdr";
	public static final String WISKOPDR_JAR = "wiskopdr.jar";

	private static SwingWorker<Class<?>, ?> worker;
	public static Class<?> getInstance() throws ClassNotFoundException {
		try {
			init();
			return worker.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		throw new ClassNotFoundException();
	}

	public static synchronized void init() {
		if(worker != null) {
			worker = new SwingWorker<Class<?>, Void>() {

				@Override
				protected Class<?> doInBackground() throws Exception {
					return Loader.create(WISKOPDR_JAR).loadClass(WISKOPDR);
				}
			};
		}
		
	}
	
}
