package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class LoggingFailure implements Failure, Runnable {
	
	final private Logger LOG;
	final private EventBus eventBus;

	public LoggingFailure(Logger lOG, EventBus eventBus) {
		LOG = lOG;
		this.eventBus = eventBus;
	}
	@Override
	public void fail(Promise<?> resolved) throws Exception {
	    Throwable fail = resolved.getFailure();
	    
	    while (fail instanceof FailedPromisesException)
	    {
	      FailedPromisesException fromPromises = (FailedPromisesException) fail;
	      Collection<Promise<?>> collection = fromPromises.getFailedPromises();
	      if(collection.size() == 1) {
	        fail = collection.iterator().next().getFailure();
	      } else {
	    	  Set<String> set = messages(collection);
	        String message = "";
	        for(String item: set) {
	          message += "\n" + item;
	        }
	        LOG.log(Level.SEVERE, message, fromPromises);
	        eventBus.fireEvent(new AlertDialogWithOKEvent(message));
	        return;
	      }
	    }
	    if (fail instanceof Dwo2Exception) {
	        LOG.log(Level.SEVERE, fail.getMessage(), fail);
	        Runnable callback = null;	        
			Dwo2Exception ex = (Dwo2Exception) fail;
			if (ex.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded) callback = this;
			eventBus.fireEvent(new AlertDialogWithOKEvent(ex, callback));
	    } else {
	        LOG.log(Level.SEVERE, fail.getMessage(), fail);
	        eventBus.fireEvent(new AlertDialogWithOKEvent(fail.toString()));
	        //throw directly
	    }
	}

	private Set<String> messages(Collection<Promise<?>> collection) {
        collection = new ArrayList<>(collection);
        Set<String> set = new TreeSet<>();
        Iterator<Promise<?>> it = collection.iterator();
        while (it.hasNext()) {
          Promise<?> p =  it.next();
          Throwable failure = p.getFailure();
          if (failure instanceof FailedPromisesException) {
        	  set.addAll(messages( ((FailedPromisesException) failure).getFailedPromises()));
          } else 
          {  if (failure instanceof Dwo2Exception)
        		  set.add(failure.getLocalizedMessage() );
        	  else 
        		  set.add(failure.toString());
        }}
        return set;
	}

	@Override
	public void run() {
		eventBus.fireEvent(new SwitchViewEvent(SelectedView.LOGOUT));	
	}
}