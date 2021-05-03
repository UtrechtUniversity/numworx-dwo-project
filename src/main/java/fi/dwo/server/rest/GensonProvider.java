package fi.dwo.server.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxb.JAXBBundle;

import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

/**
 *  For date compatibility with Resty in gwt-clients.
 * 
 * @author Gert van der Plas
 */
@Provider
public class GensonProvider extends RestyDateTimeFormat implements ContextResolver<Genson> {
	private static final DateFormat yourDateFormat = new SimpleDateFormat(RESTY_DATETIME_FORMAT);
	private final Genson genson = new GensonBuilder()
			.withBundle(new JAXBBundle())
			.useDateFormat(yourDateFormat).useDateAsTimestamp(DATE_AS_TIMESTAMP).create();

	@Override
	public Genson getContext(Class<?> type) {
		return genson;
	}
}