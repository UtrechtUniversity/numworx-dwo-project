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
String cdn = System.getProperty("CDNURL","https://cdn.dwo.nl");
%>
    <script type="text/javascript" src="/dwo/apps/deploy.jsp" ></script>
    <link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/DWOplayer.css">
    <script type="text/javascript" src="<%=cdn%>/apps/WidgetPlayer/WidgetPlayer.nocache.js"></script>
</head>
<body></body>
</html>