<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomSamlUser" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="edu.uoc.elc.lti.tool.*" %>
<%@ page import="fi.servlet.lti.*" %><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tool LTI 1.3 after registration</title>
  <style>
  	iframe {
  		border: 0px;
  	}
    #headerpane {
    	display: none;
    }
	#bodypane {
		position: absolute;
		bottom: 0px;
		width: 100%;
		height: 100%;
		margin: 0px;
	}
  </style> 
  <script>
	function logout() {
		window.location = document.getElementById("return_url").href
	}
  </script>

</head>
<body>
<%! 
	private DbAccess instance;
	
	private DbAccess getDbAccess() {
		if(instance == null) {
			instance = new DbAccess(getServletContext());
		}
		return instance;
	}
%>
<% 
	String auth = "";
	
	String return_url = "about:blank";
	DomSamlUser u = getDbAccess().getSamlAuthorization(request);
	if (u == null) {
		response.sendError(400);
		return;
	}
 { 
	 String lti_id = u.getSamlUserId();
	 String org_id = u.getSamlOrgId();
	 String authToken = u.getAuthToken();
	 String t = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
     t = java.util.Base64.getEncoder().encodeToString(t.getBytes(StandardCharsets.UTF_8));
	 auth = "&a=" + t;
 }

	Object language = session.getAttribute("tool13.tool.language");if (language == null) language = "nl";	
	Object profile =  session.getAttribute("tool13.tool.profile"); if (profile == null) profile = "77";
// if not nummeric, convert with dwoprofilecache.
	Object sconr = session.getAttribute("tool13.tool.sconr");if (sconr == null) sconr = "";
%>

<div id='headerpane' >
<a id='return_url' href='<%=return_url%>'>Logout</a>
</div>
<iframe id='bodypane'
	src="player.jsp?header=less&profile=<%=profile %><%=auth %>&locale=<%=language%><%=sconr%>"
>
</iframe>
</body>
</html>