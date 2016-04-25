package fi.servlet.lti;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RestHandler {
	private URL endpoint;

	static final String UTF8 = "UTF-8";

	static void encode(String key, char value, StringBuilder sb)
	{
	    encode(key, String.valueOf(value), sb);
	}

	static void encode(String key, int value, StringBuilder sb)
	{
	    encode(key, String.valueOf(value), sb);
	}

	static void encode(String key, String value, StringBuilder sb)
	{
	    try
	    {
	        sb.append(URLEncoder.encode(key, UTF8));
	        sb.append("=");
	        sb.append(URLEncoder.encode(value, UTF8));
	    } catch (UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
	}

	
	
	public RestHandler(URL endpoint) {
		this.endpoint = endpoint;
	}
	
	public RestHandler(String endpoint) throws MalformedURLException {
		this(new URL(endpoint));
	}
	
	public RestHandler()  {
		try {
			endpoint = new URL("http://dummytwo.dwo.nl/dwo/rest/");
		} catch (MalformedURLException e) {
		}
	}
	
	public String registerSAML(
			String lti_id,
			String org_id,
			String user_id,
			String first, String middle, String last,
			String email,
			String role,
			String schoolID,
			String className
			)
	{
		StringBuilder sb = new StringBuilder();
		
		
		
		
		return "";
	}
	
}
