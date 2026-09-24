<html>
<head>
  <title>Digital Mathematical Environment</title>
  <script>
	var API_1484_11 = {
		Initialize: function(ignore) { return "true"; },
		Terminate:  function(ignore) { return "true"; },
		Commit: function(ignore) { return "true"; },
		GetValue: function(key)  { return "";  },
		SetValue: function(key, value) { return "true"; },
		GetLastError: function(ignore) { return "0"; },
		GetErrorString: function(code) { return "no error"; },
		GetDiagnostic:  function(code) { return "no diagnostic"; }
	}
	window.API_1484_11 = API_1484_11
	function logout() {
		window.location = document.getElementById("return_url").href
	}
  </script>
  <style>
  	iframe {
  		border: 0px;
  	}
    #headerpane {
    	display: none;
    }
	#bodypane {
		width: 100%;
		height: 100%;
		margin: 0px;
	}
  </style> 
</head>
<body style="font-family:sans-serif">
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="io.jsonwebtoken.*" %>
<%@ page import="fi.servlet.lti.*" %>
<%@ page import="edu.uoc.elc.lti.tool.*" %>
<%@ page import="edu.uoc.elc.lti.platform.ags.*" %>
<%@ page import="edu.uoc.elc.lti.platform.*" %>
<%@ page import="edu.uoc.lti.deeplink.content.*" %>
<%@ page import="edu.uoc.elc.lti.tool.deeplinking.*" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomSamlUser" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%! 
	private DbAccess instance;
	
	private DbAccess getDbAccess() {
		if(instance == null) {
			instance = new DbAccess(getServletContext());
		}
		return instance;
	}
		
	void println(JspWriter out, Object o ) {
		try {
			out.println(o);
		} catch(Exception io) {
			
		}
}
%>
<%
  Tool tool = ProviderInfo.getTool(request); // (Tool) session.getAttribute("tool");

  Enumeration<String> en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
   /// out.println(paramName + " = " + request.getParameter(paramName) );
  }

  String token = request.getParameter("id_token");
  String state = request.getParameter("state");
  String auth = "";
  boolean valid = tool.isValid() || tool.validate(token, state);
  if (valid) {
    if (tool.isDeepLinkingRequest()) {
    	
    } else {
    	DomSamlUser u = getDbAccess().setEntreeCookie(tool, request, response);
    	if (u == null) return;
		 { 
			 String lti_id = u.getSamlUserId();
			 String org_id = u.getSamlOrgId();
			 String authToken = u.getAuthToken();
			 String t = "3\f" + lti_id + '\f' + org_id + '\f' + authToken;
		     t = java.util.Base64.getEncoder().encodeToString(t.getBytes(StandardCharsets.UTF_8));
    		 auth = "&a=" + t;
	   }
    }
  } else {
	   response.sendError(400, tool.getReason());
	   return;
  }
  Settings presentation = tool.getDeepLinkingSettings();
  String return_url = 
		  presentation == null ? "about:blank" :
		  presentation.getDeep_link_return_url();
  String language = tool.getLocale(); if (language == null) language = "nl";
  // FIXME width and height from claimsaccessor enum/class Presentation
  int width = 0 /*presentation.getWidth()*/;
  int height = 0  /*presentation.getHeight()*/;
// fullscreen
  if (width == 0) width = 1024;
  if (height == 0) height = 768;

  String provider = tool.getPlatform().getName();
  String sco  = tool.getCustomParameter("sco");
  String course = tool.getCustomParameter("course");
  String profile =  tool.getCustomParameter("profile"); if (profile == null) profile = "77";
  String sconr = "#LoginPlace:";
  if(sco != null) sconr = "#LoginPlace:s/" + sco;
  else if(course != null) sconr = "#LoginPlace:c/" + course;
%>
<div id='headerpane' >
<a id='return_url' href='<%=return_url%>'>Logout</a>
</div>
<iframe id='bodypane'
	src="player.jsp?header=less&profile=<%=profile %><%=auth %>&locale=<%=language%><%=sconr%>"
>
</iframe>
