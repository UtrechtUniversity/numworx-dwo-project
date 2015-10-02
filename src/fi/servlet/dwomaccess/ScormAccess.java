package fi.servlet.dwomaccess;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.scorm.ScormAdapter;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;

public class ScormAccess extends Servlet implements ScormAccessIF {

	private static final String SUSPEND_DATA = "suspendData";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static private String[] KEYS = { 
		"cmi.suspend_data",
		"cmi.score.raw",
		"cmi.total_time",
		"cmi.location",
		"cmi.completion_status"
	};
	
	static private final Properties CONVERT = new Properties();
	static private final CmiConvert CMI = new CmiConvert(); // utility class

	private static final String SESSION_TIME = "session_time";
	private static final String CMI_SESSION_TIME = "cmi." + SESSION_TIME;
	private static final String TOTAL_TIME = "total_time";
	private static final String CMI_TOTAL_TIME = "cmi." + TOTAL_TIME;
	
	static {
		CONVERT.put("cmi.suspend_data", SUSPEND_DATA);
		CONVERT.put("cmi.score.raw", "score");
		CONVERT.put(CMI_SESSION_TIME, SESSION_TIME);
		CONVERT.put(CMI_TOTAL_TIME, TOTAL_TIME);
	}

	static private String c(String key) {
		return CONVERT.getProperty(key, key);
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		access = DbAccessFactory.getDbAccess(getServletContext());
		unLock();
	}
	private DbAccessIF access;
	
	public boolean Commit(int userID, int scoID, Hashtable map) throws Exception {
		
		Set entryset = map.entrySet();
		for (Iterator iterator = entryset.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();

			String iDataModelElement = c(entry.getKey().toString());
			String iValue = entry.getValue().toString();
			if(SESSION_TIME.equals(iDataModelElement) || TOTAL_TIME.equals(iDataModelElement))
			{
				iValue = CMI.to1_2Timex(CMI.from2004Time(iValue)); // sessiontime in 1.2 format.
			}
			if(iDataModelElement.equals(SUSPEND_DATA))
			{
				iValue = convertUEsc(iValue);
			}
			
			access.LMSSetValue(scoID, userID, iDataModelElement, iValue);
		}
		return true;
	}
	
	// replace chars > 100 with \ u escapes
	
	static String convertUEsc(String s) {
		char[] charArray = s.toCharArray();
		int length = charArray.length;
		int start = 0;
		for ( ; start < length; start ++) {
			if (needEscape(charArray[start])) break;
		}
		if( start == length ) return s;
		StringBuilder b = new StringBuilder();
		if( start > 0)
			b.append(charArray, 0, start);	
		for( ; start < length; start ++ ){
			char c = charArray[start];
		    if( needEscape(c)  ){
		        b.append( "\\u" ).append( toHexString(c) );
		    }else{
		        b.append( c );
		    }
		}
		return b.toString();	}

	private static String toHexString(char c) {
		String r = Integer.toHexString(c);
		while( r.length() < 4) r = '0' + r;
		return r;
	}

	private static boolean needEscape(char c) {
		return c > '\u00FF' // || c < ' '
		;
	}

	@SuppressWarnings("unchecked")
	public Hashtable Initialize(int userID, int scoID) throws Exception {
		Hashtable map = new Hashtable();
		for (int i = 0; i < KEYS.length; i++) {
			String key = KEYS[i];
			String value = access.LMSGetValue(scoID, userID, c(key));
			if(CMI_TOTAL_TIME.equals(key))
				value = CMI.to2004Timex(CMI.from1_2Timex(value));
			if(value.length()>0)
				map.put(key, value);
		}
		return map;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().print(Arrays.asList(KEYS));
	}
	 
	// Common 
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
/*
 * Access-Control-Allow-Origin: http://foo.example
 * Access-Control-Allow-Methods: POST, GET, OPTIONS
 * Access-Control-Allow-Headers: Origin, content-type
 */
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
		resp.setHeader("Access-Control-Expose-Headers", "content-type");
		resp.setHeader("Access-Control-Allow-Headers", "origin, content-type");
// XXX werkt ook niet Access-Control-Allow-Credentials true	
//		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setContentType("text/plain");
		resp.getOutputStream().close();
	}
		
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		if("POST".equals(req.getMethod()))
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Access-Control-Expose-Headers", "content-type");
		//logHeaders(req);
		super.service(req, resp);
	}
		
//	private void logHeaders(HttpServletRequest req) {
//		Enumeration<?> e = req.getHeaderNames();
//		while (e.hasMoreElements()) {
//			String key = (String) e.nextElement();
//			Enumeration<?> values = req.getHeaders(key);
//			while (values.hasMoreElements()) {
//				Object object = values.nextElement();
//				log (key + ": " + object);
//			}
//		}
//	}
	
	static class CmiConvert extends ScormAdapter {

		protected CmiConvert() {
			super(true);
		}

		@Override
		public String GetValue(String cmiElement) {
			return null;
		}

		@Override
		public String SetValue(String key, String value) {
			return null;
		}
	
		protected long from1_2Timex(String str) {
			return super.from1_2Time(str);
		}

		protected String to1_2Timex(long time) {
			return super.to1_2Time(time);
		}

		protected String to2004Timex(long time) {
			return super.to2004Time(time);
		}

	}
}
