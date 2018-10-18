package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
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
	    
	    if (fail instanceof FailedPromisesException)
	    {
	      FailedPromisesException fromPromises = (FailedPromisesException) fail;
	      Collection<Promise<?>> collection = fromPromises.getFailedPromises();
	      if(collection.size() == 1) {
	        fail = collection.iterator().next().getFailure();
	      } else {
	        String message = "Multiple failures";
	        for(Promise<?> item: collection) {
	          message += "\n" + item.getFailure().getMessage();
	        }
	        LOG.log(Level.SEVERE, message, fromPromises);
	        eventBus.fireEvent(new AlertDialogWithOKEvent(message));
	        return;
	      }
	    }
	    if (fail instanceof Dwo2Exception) {
	        LOG.log(Level.SEVERE, fail.getMessage(), fail);
	        eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
	    } else {
	        LOG.log(Level.SEVERE, fail.getMessage(), fail);
	        eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
	        //throw directly
	    }
	}
}