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
	String launch_url = "http://spindle.fisme.uu.nl:8080/DWOmAccess/lti/tool13.jsp";
//	launch_url = response.encodeURL(launch_url);
//	launch_url = launch_url.replace(";", "%3B");
//	//session.setAttribute("tool", info.tool);
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