<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Base64"%>
<%@page import="java.net.URLEncoder"%>
<%@ page import="fi.dwo.server.db.TStamp" %>   
<%@ page import='java.util.regex.*' %>
<%@ page import="fi.dwo.server.db.Util" %>
<%! String base, locale, profile; %>
<%
	String query ;
	long tstamp = TStamp.BOOT;
	query = "?base=" + base + "&locale=" + locale + "&profile=" + profile + "&t=" + tstamp;
	String a = request.getParameter("a");
	if (a != null) {
		query += "&a=" + URLEncoder.encode(a);
	}
		
	String hash = request.getParameter("hash");
	String player = "/gwtclient/index.html";

	if ( !Util.illegal(hash)) // Deeplink
		player = "/dwo/tablet/DWOplayer.jsp";
	else
		hash = "";
%>   