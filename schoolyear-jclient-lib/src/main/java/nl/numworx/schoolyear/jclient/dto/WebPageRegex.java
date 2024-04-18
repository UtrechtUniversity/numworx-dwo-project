package nl.numworx.schoolyear.jclient.dto;

import java.util.Map;

public class WebPageRegex {
	public static final String TYPE = "web_page_regex";
	public String protocol, username, password, port, pathname, hash, hostname;
	/**
	 * undefined.
	 */
	public Map<String,String> search_params;
}
