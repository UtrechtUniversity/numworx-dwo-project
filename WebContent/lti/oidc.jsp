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
	String launch_url = "http://localhost:8081/DWOmAccess/lti/tool13.jsp";
	session.setAttribute("tool", info.tool);
%>
</head>
<body>
<ul>
	<li>iss: <%=request.getParameter("iss") %>
	<li>login_hint: <%=request.getParameter("login_hint") %>
	<li>target_link_uri: <%=request.getParameter("target_link_uri") %>
	<li>lti_message_hint: <%=request.getParameter("lti_message_hint") %>
	<p> to <a href='<%=info.redirect_url(launch_url, request) %>'>login</a>
</ul>
</body>
</html>