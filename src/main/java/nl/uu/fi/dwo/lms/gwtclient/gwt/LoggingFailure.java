package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class LoggingFailure implements Failure {
	
	final private Logger LOG;
	final private EventBus eventBus;

	public LoggingFailure(Logger lOG, EventBus eventBus) {
		LOG = lOG;
		this.eventBus = eventBus;
	}
	@Override
	public void fail(Promise<?> resolved) throws Exception {
	    Throwable fail = resolved.getFailure();
	    if (fail instanceof Dwo2Exception) {
	        LOG.log(Level.SEVERE, fail.getMessage());
	        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
	    } else {
	        LOG.log(Level.SEVERE, fail.getMessage());
	        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
	        //throw directly
	    }
	}
}