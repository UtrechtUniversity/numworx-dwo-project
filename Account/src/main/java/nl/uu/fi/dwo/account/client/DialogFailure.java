package nl.uu.fi.dwo.account.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class DialogFailure implements Failure {

	private final EventBus bus;
	private static final Logger LOG = Logger.getLogger(DialogFailure.class.getName());

	@Inject public DialogFailure(EventBus bus) {
		this.bus = bus;
	}

	@Override
	public void fail(Promise<?> resolved) {
		Throwable t = resolved.getFailure();
		LOG.log(Level.WARNING, "fail ", t);
		DialogEvent ev;
		if(t instanceof Dwo2Exception ) 
		{	Dwo2Exception e = (Dwo2Exception) t;
			ev = new DialogEvent(e);
		} else {
			Dwo2Exception e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, t.getMessage());
			e.initCause(t);
			ev = new DialogEvent(e);
		}
		bus.fireEvent(ev);		
	}
}