<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.regex.*" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
body {
	background-color: transparent !important;
}
</style>

<%
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
String cdn = System.getProperty("CDNURL","https://cdn.dwo.nl");
%>
    <script type="text/javascript" src="/dwo/apps/deploy.jsp" ></script>
    <link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/DWOplayer.css">
<% if (!name.isEmpty()) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/<%=name %>.css" ><%}%>    

    <script type="text/javascript" src="<%=cdn%>/apps/WidgetPlayer/WidgetPlayer.nocache.js"></script>

</head>
<body></body>
</html>