<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.regex.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Widget</title>
<%
	String profile = request.getParameter("profile");
	if(profile == null||profile.isEmpty()) profile="77";
	else if (!Pattern.matches("\\d+", profile)) {
	  response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  return;
	}
	String cdn = System.getProperty("CDNURL","https://ebs-dev-dwo-nl.s3.eu-west-1.amazonaws.com");
	String remote = "https://teuniz.dwo.nl";
%>
    <script type="text/javascript" src="<%=remote %>/dwo/apps/deploy.jsp" ></script>
    <link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/DWOplayer.css">
<% if ("111".equals(profile)) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/inf.css" ><%}%>    

    <script type="text/javascript" src="<%=cdn%>/apps/WidgetPlayer/WidgetPlayer.nocache.js"></script>

</head>
<body>

</body>
</html>