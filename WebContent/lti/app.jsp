<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fi.dwo.server.persistence.DbAccessLocal"%>
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
  </script> 
</head>
<body style="font-family:sans-serif">
<img src="http://www.sun.com/images/l2/l2_duke_java.gif" align="right">
<p><b>IMS BasicLTI Java Provider</b></p>
<p>This is a very simple reference implementaton of the tool side (i.e. provider) for IMS BasicLTI.</p>
<p>This tool is configured with an LMS-wide guid of "lmsng.school.edu" protected by a secret of "secret".
For this tool, all resource level secrets are also "secret".</p>
</p>
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
<pre>
<%

  Enumeration en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
    out.println(paramName + " = " + request.getParameter(paramName) );
  }

  OAuthMessage oam = OAuthServlet.getMessage(request, null);
  OAuthValidator oav = new SimpleOAuthValidator();
  String oauth_consumer_key = request.getParameter("oauth_consumer_key");
  if ( oauth_consumer_key == null ) {
    doReturn(request, response, "Missing oauth_consumer_key", out);
    return;
  }
  OAuthConsumer cons = null;
  if ( "lmsng.school.edu".equals(oauth_consumer_key) ) {
    cons = new OAuthConsumer("http://call.back.url.com/", "lmsng.school.edu", "secret", null);
  } else if ( "12345".equals(oauth_consumer_key) ) {
    cons = new OAuthConsumer("http://call.back.url.com/", "12345", "secret", null);
  } else {
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
    out.println("\n<b>Base Message</b>\n</pre><p>\n");
    out.println(OAuthSignatureMethod.getBaseString(oam));
    out.println("<pre>\n");
    oav.validateMessage(oam,acc);
    out.println("Message validated");
  } catch(Exception e) {
	    doReturn(request, response, "Error while valdating message", out);
	    log("Error validating message", e);
	    return;
  }

  String sconr = "";
  String info = request.getPathInfo();
  if(info.startsWith("/sco/")) sconr = info.substring(5);
  
%>
</pre>
<hr>
<div id='headerpane' ></div>
<iframe id='bodypane'
	src="/dwo/apps/player.html#<%=sconr%>" width="800" height="600"
>
</iframe>
<hr>
<p>
Note: Unpublished drafts of IMS Specifications are only available to IMS members and any software based on
an unpublished draft is subject to change.
Sample code is provided to help developers understand the specification more quickly.
Simply interoperating with this sample implementation code does not
allow one to claim compliance with a specification.
<p>
<a href=http://www.imsglobal.org/toolsinteroperability2.cfm>IMS Learning Tools Interoperability Working Group</a> <br/>
<a href="http://www.imsglobal.org/ProductDirectory/directory.cfm">IMS Compliance Detail</a> <br/>
<a href="http://www.imsglobal.org/community/forum/index.cfm?forumid=11">IMS Developer Community</a> <br/>
<a href="http:///www.imsglobal.org/" class="footerlink">&copy; 2009 IMS Global Learning Consortium, Inc.</a> under the Apache 2 License.</p>

