<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
Dit is toets.jsp
<br>
<%
String configHash  = request.getHeader("X-SafeExamBrowser-ConfigKeyHash");
Object source = request.getAttribute("fi.dwo.server.xss.confighash");
Object url = request.getRequestURI();
Object seb = request.getAttribute("fi.dwo.server.xss.seb");
%>
config hash <%=configHash %>
<br>
config source <%=source %>
<br>
url <%= url %>
<br>
seb <%= seb %>

</body>
</html>