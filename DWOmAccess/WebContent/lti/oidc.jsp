<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>OIDC thirdparty login</title>
<%@ page import="fi.servlet.lti.ProviderInfo" %>


<%
	ProviderInfo info = ProviderInfo.get(request);
	String launch_url = "/DWOmAccess/lti/tool13.jsp";
	String redirect_url = info.redirect_url(launch_url, request);
%>
<meta http-equiv="refresh" content='0;url=<%=redirect_url%>'>
</head>
<body>
<!-- <ul> -->
<-- 	<li>iss: <%=request.getParameter("iss") %> -->
<-- 	<li>login_hint: <%=request.getParameter("login_hint") %> -->
<-- 	<li>target_link_uri: <%=request.getParameter("target_link_uri") %> -->
<-- 	<li>lti_message_hint: <%=request.getParameter("lti_message_hint") %> -->
<!-- </ul> -->
	<p>to <a href='<%=redirect_url%>'>login</a>
</body>
</html>