<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); %>
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
  </style> 
</head>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URL" %>
<%@ page import="net.oauth.OAuth" %>
<%@ page import="net.oauth.OAuthMessage" %>
<%@ page import="net.oauth.OAuthConsumer" %>
<%@ page import="net.oauth.OAuthAccessor" %>
<%@ page import="net.oauth.OAuthValidator" %>
<%@ page import="net.oauth.SimpleOAuthValidator" %>
<%@ page import="net.oauth.signature.OAuthSignatureMethod" %>
<%@ page import="net.oauth.server.HttpRequestMessage" %>
<%@ page import="net.oauth.server.OAuthServlet" %>
<%@ page import="net.oauth.signature.OAuthSignatureMethod" %>
<%@ page import="fi.servlet.lti.DbAccess" %>
<%@ page import="fi.servlet.dwomaccess.DbAccessFactory" %>
<%
        String width = request.getParameter("launch_presentation_width");
        if(width == null || width.isEmpty()) width="100%";
        String height = request.getParameter("launch_presentation_height");
        if(height == null || height.isEmpty()) height="100%";
%>
<body style="font-family:sans-serif; margin:0px; position: absolute; width: <%=width%>;height:<%=height%>;">
<%!
private DbAccess instance;

private DbAccess getDbAccess() {
	if(instance == null) {
		instance = new DbAccess(DbAccessFactory.getDbAccess(getServletContext()));
	}
	return instance;
}

private void doReturn(HttpServletRequest request, HttpServletResponse response, 
        String s, JspWriter out)
		throws java.io.IOException
	{
		String return_url = request.getParameter("launch_presentation_return_url");
		if ( return_url != null && return_url.length() > 1 ) {
			if ( return_url.indexOf('?') > 1 ) {
				return_url += "&lti_msg=" + URLEncoder.encode(s);
			} else {
				return_url += "?lti_msg=" + URLEncoder.encode(s);
			}
			response.sendRedirect(return_url);
			return;
		}
		out.print("<p>");
		out.print(s);
		out.println("</p>");
	}

%>
<%

//   Enumeration en = request.getParameterNames();
//   while (en.hasMoreElements()) {
//     String paramName = (String) en.nextElement();
//     out.println(paramName + " = " + request.getParameter(paramName) );
//   }

  OAuthMessage oam = OAuthServlet.getMessage(request, null);
  OAuthValidator oav = new SimpleOAuthValidator();
  String oauth_consumer_key = request.getParameter("oauth_consumer_key");
  if ( oauth_consumer_key == null ) {
    doReturn(request, response, "Missing oauth_consumer_key", out);
    return;
  }
  OAuthConsumer cons = null;
  {
	  String secret = getDbAccess().getSecret(oauth_consumer_key);
	  if ( secret != null ) {
	    cons = new OAuthConsumer("about:blank", oauth_consumer_key, secret, null);
	  } else {
	    doReturn(request, response, "Key "+oauth_consumer_key+" not found", out);
	    return;
	  }
  }

  OAuthAccessor acc = new OAuthAccessor(cons);

  try {
//     out.println("\n<b>Base Message</b>\n</pre><p>\n");
//     out.println(OAuthSignatureMethod.getBaseString(oam));
//     out.println("<pre>\n");
    oav.validateMessage(oam,acc);
//     out.println("Message validated");
  } catch(Exception e) {
	    doReturn(request, response, "Error while valdating message", out);
	    log("Error validating message", e);
	    return;
  }

  String sconr = "#LoginPlace:";
  String info = request.getPathInfo();
  if(info != null) {
  	if(info.startsWith("/sco/")) sconr = "#LoginPlace:s/" + info.substring(5);
  	else if(info.startsWith("/course")) sconr = "#LoginPlace:c/" + info.substring(8);
  }
	String language = request.getParameter("launch_presentation_locale");
	if(language != null) {
		int index = language.indexOf('-'); // remove region
		if(index >= 0) language = language.substring(0,index);
	}
	if(language == null || language.isEmpty()) 
	language = "nl";

	String profile  = request.getParameter("custom_profile");
	if(profile == null)
		profile = "77";

  	getDbAccess().setCookie(request, response);  
%>
<div id='headerpane' >
<a  id='return_url'
	href='<%=request.getParameter("launch_presentation_return_url") %>'>Logout</a>
</div>
<iframe id='bodypane'
	src="player.jsp?profile=<%=profile %>&locale=<%=language%><%=sconr%>" width="<%=width%>" height="<%=height%>"
>
</iframe>
</body>
</html>
