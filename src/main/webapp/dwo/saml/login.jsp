<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.Cookie" %>
<%@ page import="org.imsglobal.basiclti.BasicLTIUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	String cookies = "document.cookie";
	String token = "'bearer token'";
	Cookie cookie = new Cookie("aap","noot");
	response.addCookie(cookie);
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/dwo/tablet/scripts/SCORM_12_APIWrapper.js"></script>
<title>Please login</title>
</head>
<body>
<script type="text/javascript">


function later() {
	var cookies = (<%=cookies %>);
	var token = (<%=token %>);
	console.log(cookies);
	console.log(token);
	try {
		doLMSInitialize();
		doLMSSetValue("dme.token", token);
		doLMSSetValue("dme.cookies", cookies);
		doLMSSetValue("cmi.exit", "logout");
		doLMSFinish("");
	} catch(e) {
		alert(e);
	}
}
setTimeout("later()",5)
</script>
<%
	String display = "W<i>m";
	out.print("<h1>Welcome ");
	out.print(BasicLTIUtil.htmlspecialchars(display));
	out.println("</h1>");
%>
</body>
</html>