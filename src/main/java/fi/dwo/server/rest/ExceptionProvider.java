package fi.dwo.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Provider
public class ExceptionProvider implements javax.ws.rs.ext.ExceptionMapper<Exception> {
	static Logger LOG = Logger.getLogger(ExceptionProvider.class.getName());

	@Override
	public Response toResponse(Exception exception) {
// no exceptional logging with "no such user/wrong password" 
		if (exception instanceof WebApplicationException) {
			WebApplicationException ex = (WebApplicationException) exception;
			if (401 == ex.getResponse().getStatus()) 
				return ex.getResponse();
		}
		
	    if (exception instanceof NotFoundException) {
	      LOG.log(Level.WARNING, "Not Found Exception " + exception.getLocalizedMessage());
	    } else
	      LOG.log(Level.SEVERE, "Unhandled exception", exception);
        if (exception instanceof Dwo2Exception) {
          // wrap
          exception = new Dwo2RestException( (Dwo2Exception) exception );
        }
		if(exception instanceof WebApplicationException ) {
			return ((WebApplicationException) exception).getResponse();
		}
        return Response.status(500).build();
	}
}
