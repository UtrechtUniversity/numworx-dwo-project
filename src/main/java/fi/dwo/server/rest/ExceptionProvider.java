package fi.dwo.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionProvider implements javax.ws.rs.ext.ExceptionMapper<Exception> {
	static Logger LOG = Logger.getLogger(ExceptionProvider.class.getName());

	@Override
	public Response toResponse(Exception exception) {
		LOG.log(Level.SEVERE, "Unhandled exception", exception);
		if(exception instanceof WebApplicationException ) {
			return ((WebApplicationException) exception).getResponse();
		}
        return Response.status(500).build();
	}
}
