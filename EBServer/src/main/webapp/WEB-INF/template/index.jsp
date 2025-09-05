<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull" %>
<%
	DomDwoProfileFull profile = (DomDwoProfileFull)request.getAttribute("template.profile");
	String url = (String) request.getAttribute("template.url");
	Long id = (Long) request.getAttribute("template.profile.id");
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=profile.getDwoProfileDescription()%></title>
</head>
<body>
	dit is <%=url%> voor <%=id %> ? with = <%= request.getParameter("with") %>
</body>
</html>