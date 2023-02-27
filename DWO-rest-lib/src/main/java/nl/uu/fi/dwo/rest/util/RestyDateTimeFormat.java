package nl.uu.fi.dwo.rest.util;

public abstract class RestyDateTimeFormat {

	protected RestyDateTimeFormat() {
	}

	/**
	 * Format to use for Resty compatible exchange of Dates.
	 * 
	 * @see org.fusesource.restygwt.client.Defaults
	 */
	public final static String RESTY_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	/**
	 * Option for genson builder.
	 */
	public static final boolean DATE_AS_TIMESTAMP = false;

}
