<%@page import="fi.dwo.server.persistence.DbAccessLocal"%>
<html>
<head>
  <title>Digitale Wiskunde Omgeving</title>
  <%
  	String css = request.getParameter("launch_presentation_css_url");
    if(css != null && !css.isEmpty())
    {
    	%>
    	<link type="text/css" rel="stylesheet" href="<%=css%>">
    	<% 
    }
  %>
</head>
<body>

<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.net.URLEncoder" %>
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

<%!
	private DbAccess dbAccess = new DbAccess(
			new DbAccessLocal()
	);

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
/* */
  Enumeration en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
    out.println(paramName + " = " + request.getParameter(paramName) );
  }
  out.println("path info = " + request.getPathInfo() );
/* */
%>
</pre>
<%
  OAuthMessage oam = OAuthServlet.getMessage(request, null);
  OAuthValidator oav = new SimpleOAuthValidator();
  String oauth_consumer_key = request.getParameter("oauth_consumer_key");
  if ( oauth_consumer_key == null ) {
    doReturn(request, response, "Missing oauth_consumer_key", out);
    return;
  }
  OAuthConsumer cons = null;
  String secret = dbAccess.getSecret(oauth_consumer_key);
  if ( secret != null ) {
    cons = new OAuthConsumer("about:blank", oauth_consumer_key, secret, null);
  } else {
    doReturn(request, response, "Key "+oauth_consumer_key+" not found", out);
    return;
  }

  OAuthAccessor acc = new OAuthAccessor(cons);

  try {
    oav.validateMessage(oam,acc);
  } catch(Exception e) {
    doReturn(request, response, "Error while valdating message", out);
    log("Error validating message", e);
    return;
  }
// applet gegevens
 	String language = request.getParameter("launch_presentation_locale");
	if(language != null) {
		int index = language.indexOf('-'); // remove region
		if(index >= 0) language = language.substring(0,index);
	}
	if(language == null || language.isEmpty()) 
		language = "nl";
	
	String profile  = request.getParameter("custom_profile");
	if(profile == null)
		profile = "1";
	
  	String logoutURL = request.getParameter("launch_presentation_return_url");
  	
  	String width = request.getParameter("launch_presentation_width");
  	if(width == null || width.isEmpty()) width="100%";
 	String height = request.getParameter("launch_presentation_height");
  	if(height == null || height.isEmpty()) height="100%";
  	
// 	<param name="SERVLET" value="/servlet/fi.dwo.server.persistence.DbAccessServlet" >

  	dbAccess.setCookie(request, response);
%>
<applet
	code="fi.dwo.client.domain.DWO"
	width="<%=width %>"
	height="<%=height %>"
	archive="dwo.jar"
	mayscript
	id="API"
	name="API"
	codebase="https://ws.fisme.science.uu.nl/dwo/jars/"
	>
	Helaas, Java is niet geinstalleerd op uw computer.
	Klik <a target="_blank" href="http://java.com/nl">
	hier</a> om java te installeren.

	<param name="language" value="<%=language %>" >
	<param name="profile" value="<%= profile %>" >
	<param name='cookies' value='false' >
	<param name='logoutURL' value="<%=logoutURL %>" >
	<%= dbAccess.getDeepLink(request.getPathInfo()) %>
</applet>

</body>
</html>
