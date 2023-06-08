package nl.uu.fi.dwo.mobile.utils;

import javax.inject.Provider;

public class LoggingProvider implements Provider<Logging> {

	@Override
	public Logging get() {
		return NoLogging.instance;
	}

}
