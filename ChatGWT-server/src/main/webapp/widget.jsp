<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.regex.*" %>
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
	String cdn = System.getProperty("CDNURL","https://ebs-dev-dwo-nl.s3.eu-west-1.amazonaws.com");
%>
    <script type="text/javascript" src="/dwo/apps/deploy.jsp" ></script>
    <link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/DWOplayer.css">
<% if ("111".equals(profile)) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/inf.css" ><%}%>    

    <script type="text/javascript" src="<%=cdn%>/apps/WidgetPlayer/WidgetPlayer.nocache.js"></script>

</head>
<body></body>
</html>