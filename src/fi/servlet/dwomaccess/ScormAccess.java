package fi.servlet.dwomaccess;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.xmlrpc.Servlet;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;

public class ScormAccess extends Servlet implements ScormAccessIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static private String[] KEYS = { 
		"cmi.suspend_data", 
	};
	
	DbAccessIF access = DbAccessCreator.instance();
	
	@SuppressWarnings("unchecked")
	public boolean Commit(int userID, int scoID, Hashtable map) throws Exception {
		
		Set entryset = map.entrySet();
		for (Iterator iterator = entryset.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			access.LMSSetValue(scoID, userID, entry.getKey().toString(), entry.getValue().toString());
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable Initialize(int userID, int scoID) throws Exception {
		Hashtable map = new Hashtable();
		for (int i = 0; i < KEYS.length; i++) {
			String key = KEYS[i];
			map.put(key, access.LMSGetValue(scoID, userID, key));
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
		
		resp.setContentType("text/plain");
		resp.getOutputStream().close();
	}
		
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		if("POST".equals(req.getMethod()))
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Access-Control-Expose-Headers", "content-type");
		logHeaders(req);
		super.service(req, resp);
	}
		
	private void logHeaders(HttpServletRequest req) {
		Enumeration e = req.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Enumeration values = req.getHeaders(key);
			while (values.hasMoreElements()) {
				Object object = (Object) values.nextElement();
				log (key + ": " + object);
			}
		}
	}
	
}
