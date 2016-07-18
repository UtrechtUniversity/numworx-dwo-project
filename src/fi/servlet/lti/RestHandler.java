package fi.servlet.lti;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class RestHandler {
	private URL endpoint;

	Logger LOG = Logger.getLogger(getClass().getName());
	
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

	public RestHandler(URL endpoint) throws MalformedURLException {
		this.endpoint = new URL(endpoint,"public/user/registerSAML");
		LOG.info("endpoint = "+endpoint);
	}
	
	public RestHandler(String endpoint) throws MalformedURLException {
		this(new URL(endpoint));
	}
	
	public RestHandler()  {
		try {
			endpoint = new URL("http://dummytwo.dwo.nl/dwo/rest/public/user/registerSAML");
		} catch (MalformedURLException e) {
		}
	}
	
	public String registerSAML(
			String user_id,
			String lti_id,
			String org_id,
			String first, String middle, String last,
			String email,
			String role,
			String schoolID,
			String className
			) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		encode("userident",user_id, sb); sb.append('&');
		encode("samluserid", lti_id, sb);sb.append('&');
		encode("samlorgid", org_id, sb); sb.append('&');
		encode("gn", first, sb);         sb.append('&'); 
		encode("prefix", middle, sb);    sb.append('&');
		encode("fn", last, sb);          sb.append('&'); 
		encode("email", email, sb);      sb.append('&');
		encode("role", role, sb);        sb.append('&');
		encode("schoolID", schoolID, sb);sb.append('&');
		encode("classname", className, sb);
		HttpURLConnection uc;
		uc = (HttpURLConnection) endpoint.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		OutputStream out = uc.getOutputStream();
		LOG.info("RegisterSAML " + sb);
		out.write(sb.toString().getBytes(Charset.forName(UTF8)));
		out.flush();
		out.close();
		uc.connect();
		int status = uc.getResponseCode();
		int size = uc.getContentLength();
		LOG.info("status " + status + ", size "+ size);
		InputStream in = uc.getInputStream();
		byte[] bytes = new byte[size];
		in.read(bytes);
		in.close();
		String string = new String(bytes, Charset.forName(UTF8));
		LOG.info("result " + string);
		return string;
	}
	
}
