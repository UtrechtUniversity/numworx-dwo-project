<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String shib = (String) request.getAttribute("Shib-Handler");
	String param = request.getParameter("return");
	if (param == null) param = "/";
	param = URLEncoder.encode(param);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="refresh" content="0;<%=shib%>/Logout?return=<%=param %>">
</head>
<body>
	<a class='logout' href="<%=shib%>/Logout?return=<%=param %>" target="_parent" >Logout</a>

</body>
</html>