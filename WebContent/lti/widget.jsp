<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <title></title>
  <%
  	String css = request.getParameter("launch_presentation_css_url");
    if(css != null && !css.isEmpty())
    {
    	%>
    	<link type="text/css" rel="stylesheet" href="<%=css%>">
    	<% 
    }
  %>
  
  <style type="text/css">
  body { 
  		margin: 0;
  		overflow: scroll;
  		background-color: #FFFFFF;
  }
  </style>
</head>
<body>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="fi.servlet.lti.WidgetBean" %>
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
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.json.simple.JSONValue" %>
<jsp:useBean id="widgets" class="fi.servlet.lti.WidgetBean" scope="application"></jsp:useBean>
<%!
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
/* * /
  Enumeration en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
    out.println(paramName + " = " + request.getParameter(paramName) );
  }
  out.println("path info = " + request.getPathInfo() );
  out.println(widgets);
/* */
%>
<%
  OAuthMessage oam = OAuthServlet.getMessage(request, null);
  OAuthValidator oav = new SimpleOAuthValidator();
  String oauth_consumer_key = request.getParameter("oauth_consumer_key");
  if ( oauth_consumer_key == null ) {
    doReturn(request, response, "Missing oauth_consumer_key", out);
    return;
  }
  OAuthConsumer cons = null;
  String secret = "secret";
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
		language = "en";
	
	String launchData  = request.getParameter("custom_launch_data");
	if(launchData == null)
		launchData = "{}";

	Map map = (Map)JSONValue.parse(launchData);
	launchData = JSONValue.toJSONString(map.get("launchData"));
	launchData = StringEscapeUtils.escapeHtml(launchData);
	
  	String width = request.getParameter("launch_presentation_width");
  	if(width == null || width.isEmpty()) width="100%";
 	String height = request.getParameter("launch_presentation_height");
  	if(height == null || height.isEmpty()) height="100%";
  	
  	String className = request.getParameter("oauth_consumer_key");
  	Object r = map.get("randomValues");
  	if (r == null) r = java.util.Collections.EMPTY_LIST;
  	String randomValues = StringEscapeUtils.escapeHtml(JSONValue.toJSONString(r));
  	String uuid = request.getParameter("resource_link_id");
  	Object o = map.get("assessmentMode");
  	if(o == null) o = Integer.valueOf(0);
  	String assessmentMode = StringEscapeUtils.escapeHtml(String.valueOf(o));;
  	String learnerId = request.getParameter("user_id");
  	String learnerName = request.getParameter("lis_person_name_full");
  	Object b = map.get("background");
  	if (b == null) b = "#FFFFFF";
  	String background = StringEscapeUtils.escapeHtml(String.valueOf(b));
  	
  	String logID = StringEscapeUtils.escapeHtml(JSONValue.toJSONString(map.get("logID")));
  	Object l = map.get("logOption");
  	if(l == null) l = Boolean.FALSE;
  	String logOption = StringEscapeUtils.escapeHtml(JSONValue.toJSONString(l));
  	Object s = map.get("scoreMax");
  	if( s == null) s = Integer.valueOf(10);
  	String scoreMax = StringEscapeUtils.escapeHtml(JSONValue.toJSONString(s));
  	
  	String archive = widgets.getArchive(className);
%>
<applet
	code="org.cbook.applet.CBookInstance"
	width="<%=width %>"
	height="<%=height %>"
	archive="cbookinstance.jar, <%= archive %>"
	mayscript
	id="API"
	name="CBookInstance"
	codebase="."
	>
	Helaas, Java is niet geinstalleerd op uw computer.
	Klik <a target="_blank" href="http://java.com/nl">
	hier</a> om java te installeren.

	<param name="language" value="<%=language%>" >
	<param name="width" value="<%=width%>" >
	<param name="height" value="<%=height%>" >
	<param name="className" value="<%=className%>" >
	<param name="launchData" value='<%= launchData %>' >
	<param name="randomValues" value='<%=randomValues %>' >
	<param name="uuid" value="<%=uuid %>" >
	<param name="assessmentMode" value="<%=assessmentMode %>" >
	<param name="learner_id" value="<%=learnerId%>" >
	<param name="learner_name" value="<%=learnerName%>" >
	<param name="background" value="<%=background%>" >
	<param name="logID" value="<%=logID%>" >
	<param name="logOption" value="<%=logOption%>">
	<param name="scoreMax" value="<%=scoreMax %>" >
	
</applet>
</body>
</html>
