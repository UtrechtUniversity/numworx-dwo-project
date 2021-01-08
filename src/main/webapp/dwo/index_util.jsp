<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Base64"%>
<%@ page import='java.util.regex.*' %>
<%@ include file="/dwo/saml_util.jsp" %>
<%
	String query ;
	long tstamp = System.currentTimeMillis();
	query = "?base=" + base + "&locale=" + locale + "&profile=" + profile + "&t=" + tstamp;
	
	Cookie[] cookies = wrap.getCookies();
	String u = null, o = null, t = null;
	for(Cookie c : cookies) {
		if (DWO_SAML_ORGANIZATION_ID.equals(c.getName())) o = c.getValue();
		else if (DWO_SAML_USER_ID.equals(c.getName())) u = c.getValue();
		else if (DWO_SAML_AUTH_TOKEN.equals(c.getName())) t = c.getValue();
		//c.setHttpOnly(true);
	}
	if (o != null && u != null && t != null) {
	      t = "3\f" + u + '\f' + o + '\f' + t;
	      query += "&a=" + Base64.getEncoder().encodeToString(t.getBytes());
	}
	
	
	String hash = request.getParameter("hash");
	String player = "/gwtclient/index.html";

	if ( hash != null && (hash.isEmpty() || Pattern.matches("#[a-z]+:\\d*", hash))) // Deeplink
		player = "/dwo/tablet/DWOplayer.jsp";
	else
		hash = "";
%>   