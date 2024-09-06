<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="java.util.regex.*" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache" %>
<html>
<head>
<%
// 	String responsive = request.getParameter("responsive");
// 	String header = request.getParameter("header");

	String profile = request.getParameter("profile");
	String name = "";
	if(profile == null||profile.isEmpty()) profile="77";
	else if (!Pattern.matches("\\d+", profile)) {
	  response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  return;
	}
	try {
		DomDwoProfile dom = PublicProfileCache.get(profile);
		if (dom.getDwoProfileRights().contains("c"))
			name = dom.getDwoProfileName();
	} catch(Exception e) {
		log("get profile failed", e);
	}
	String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
%>
	<meta charset="UTF-8">
	<title>LeerdoelWidgetGWT</title>
	<script type="text/javascript" src='deploy.jsp'></script>
	<% if (!name.isEmpty()) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/<%=name %>.css" ><%}%>    
    <script>css("LeerdoelWidgetGWT.css")</script>
    <script>script("leerdoelwidgetgwt/leerdoelwidgetgwt.nocache.js")</script>
	<link href="https://fonts.googleapis.com/css?family=Ubuntu:400,400i,500,700" rel="stylesheet">
	<link rel="stylesheet" href="/gwtclient/css/styles.css" type="text/css" media="screen">
</head>
<body>

</body>
</html>