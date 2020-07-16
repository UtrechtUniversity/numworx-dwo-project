<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="org.imsglobal.basiclti.BasicLTIUtil" %>
<%@ include file="/dwo/saml_util.jsp" %>
<!DOCTYPE html>
<html>
<%
	String cookies = "document.cookie";
// Alternatief
	Cookie[] cs = wrap.getCookies();
	StringBuilder sb = new StringBuilder("\"");
	for(Cookie c: cs) {
	  sb.append( c.getName() ) 
	  .append("=")
	  .append( c.getValue())
	  .append("; ");
	}
	if (sb.length() > 2) sb.setLength(sb.length()-2);
	sb.append("\"");
	cookies = sb.toString();
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/dwo/tablet/scripts/SCORM_12_APIWrapper.js"></script>
<title>Please login</title>
</head>
<body>
<script type="text/javascript">


function later() {
	var cookies = "???";
	try {
		doLMSInitialize();
		doLMSSetValue("cmi.exit", "logout");
		doLMSFinish("");
	<%
	  try {
		  int r = Integer.parseInt(request.getParameter("r"));
		  if (r > 1024 && r <= 0xFFFF ) {
		  %>
			var location = "http://127.0.0.1:<%=r%>/local/Terminate"
			var arg = JSON.stringify({'dme.cookies': cookies, 'cmi.exit':'logout'});
			arg = encodeURIComponent(arg);
			window.location = location + "?q=" + arg
		  <%
		}} catch (Exception ignore) { }
	%>
	} catch(e) {
		alert(e);
	}
}
setTimeout("later()",1)
</script>
<%
	String display = String.valueOf(request.getAttribute("givenName"));
	out.print("<h1>Welcome ");
	out.print(BasicLTIUtil.htmlspecialchars(display));
	out.println("</h1>");
	
	if (request.getParameter("r") != null) {
	  out.println("<p>You can close this window.");
	}
	
%>
</body>
</html>