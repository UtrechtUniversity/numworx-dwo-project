<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import='java.util.regex.*' %>
<%!
%>
<%
	String query ;	
	query = "?base=" + base + "&locale=" + locale + "&profile=" + profile;
	
	String hash = request.getParameter("hash");
	String player = "/gwtclient/index.html";

	if ( hash != null && (hash.isEmpty() || Pattern.matches("#[a-z]+:\\d*", hash))) // Deeplink
		player = "/dwo/tablet/DWOplayer.jsp";
	else
		hash = "";
%>   