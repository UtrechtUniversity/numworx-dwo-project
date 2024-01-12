<%@page import="java.net.URLEncoder"%>
<%@ page import="fi.dwo.server.db.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String shib = (String) request.getAttribute("Shib-Handler");
	String param = request.getParameter("return");
	if (Util.illegal(param)) param = "/";
	String enparam = URLEncoder.encode(param);
	session.setAttribute("dwo.oauth2.prompt", "login");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
<% if (shib != null) { %>	
	<meta http-equiv="refresh" content="0;<%=shib%>/Logout?return=<%=enparam %>">
<% } else { %>
	<meta http-equiv="refresh" content="0;<%=param %>">
<% } %>
</head>
<body>
<% if (shib != null) { %>	
	<a class='logout' href="<%=shib%>/Logout?return=<%=enparam %>" target="_parent" >Logout</a>
<% } else { %>
	<a class='logout' href="<%=param %>" target="_parent" >Logout</a>
<% } %>

</body>
</html>