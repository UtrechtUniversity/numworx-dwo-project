<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="fi.dwo.server.rest.jaxrsfilters.*" %>
<%@page import="javax.ws.rs.core.*,java.security.Principal" %>
<!DOCTYPE html>
<%
	boolean valid = true;
	String token = request.getParameter("token");
	AuthenticationRequestFilter filter = new AuthenticationRequestFilter(request);
	SecurityContext ctx = new SecurityContext() {
		public boolean isUserInRole(String u) { return false; }
		public boolean isSecure() { return request.isSecure(); }
		public String getAuthenticationScheme() { return request.getAuthType(); }
		public Principal getUserPrincipal() { return null; }
	};
	ctx = filter.validateJWTToken(token, ctx);
	valid = null != ctx;
	String client_id = request.getParameter("client_id");
	valid = Objects.equals("13f8e9cc8928b3409822", client_id) && valid; // clientid prosody

    if (!valid)
		response.sendError(404);
%>

<html>
<head>
<meta charset="UTF-8">
<title>Verify Authentication</title>
</head>
<body>
A dummy page for <%=ctx.getUserPrincipal().getName() %>
</body>
</html>