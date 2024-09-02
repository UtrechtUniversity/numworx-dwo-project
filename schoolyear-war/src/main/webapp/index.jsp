<!doctype html>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<html>
  <head>
<%!
	static final String IPRANGE = System.getProperty("ENV_IPRANGE", "");
%>
    <title>Numworx for Schoolyear</title>
    <link type="text/css" rel="stylesheet" href="/uuindex.css">
  </head>
  <body>
<%
	String host = request.getRemoteAddr();
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	The device at this address <%=host %> is not allowed for assessments. Use a secured device.
<%	  
	}
	else {
%>
    <img src="/logo-nl.svg">
    <p>
    Ga naar/Go to:
    <ul>
	<li><a href="synl.jsp">Numworx (Nederlands)</a> voor studenten</li>
	<li><a href="sy.jsp">Numworx (English)</a> for students</li>
    </ul>

    <div class='footer'>
	<iframe src="/dwo/rest/public/status/version" height="34" width="450" >
    </div>
<% } %>  
  </body>
</html>
