package fi.dwo.server.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

//@Component
@Provider
public class GensonProvider implements ContextResolver<Genson> {
	private static final DateFormat yourDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final Genson genson = new GensonBuilder().useDateFormat(yourDateFormat).useDateAsTimestamp(false).create();

	@Override
	public Genson getContext(Class<?> type) {
		return genson;
	}
}