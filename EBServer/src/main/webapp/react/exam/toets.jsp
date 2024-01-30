<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ page import="java.util.*" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache" %>
<%@ include file="/dwo/saml_util.jsp" %>
<%@ include file='/dwo/toets_util.jsp' %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%
	String requestHash = request.getHeader("X-SafeExamBrowser-RequestHash");
	String configHash  = request.getHeader("X-SafeExamBrowser-ConfigKeyHash");
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");

	if(requestHash == null && needSEB ||  !Subnet.netMatchRange(IPRANGE, host) )
	{
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +  " host = " + host);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	boolean failed = true;
	String[] hashes = { "ccbb7f46b416704eeecfd7cb96c0a51c517c311ca232150ec4411968795053f3", // mac 2.1.2
						"6d2b53bcb6cae8826b8c5fd71afeb97c2b8c4e7f5d75526ca17066b96461c904",  // win 2.1.7
						System.getProperty("SEB_TOETS_MAC", ""),
						System.getProperty("SEB_TOETS_WIN", "")
	
	};
	for(String hash : hashes) {
		StringTokenizer st = new StringTokenizer(hash, ",");
		while(st.hasMoreTokens()) {
			hash = st.nextToken();
			if(hash.equals(requestHash)|| hash.equals(configHash)) failed = false;
		}
	}
	String id = request.getParameter("id");
	if(failed && needSEB && id == null)
	{
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +" hash = " + requestHash + " " + configHash);
		if(requestHash == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
	}
	String dwo_env = System.getProperty("DWO_ENV", "app");
	String defaultPlace = "";
	if (id != null) {
		try {
			defaultPlace = "cc:" + Long.valueOf(id);
		} catch(Exception e) {}
	}
	String name = "";
	String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
	try {
		DomDwoProfile dom = PublicProfileCache.get("114");
		if (dom.getDwoProfileRights().contains("c"))
			name = dom.getDwoProfileName();
	} catch(Exception e) {
		log("get profile failed", e);
	}
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css">
<% if (!name.isEmpty()) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/<%=name %>.css" ><%}%>    
    <meta name="gwt:property" content="locale=nl" >
    <script>
    	DWO_PROFILE_ID = 114
    	SECURE_MODE="SEB" // possibly others
        dwo_env = "<%=dwo_env%>"
        defaultPlace = "<%= defaultPlace %>"
    	function logout() {
    		window.location = "https://<%=server%>/react/exam/logout.html"
    	}
    </script>
    <title>Save Exam Browser</title>
    
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="/dwo/tablet/DWOplayer/DWOplayer.nocache.js"></script>
  </head>
  <body id="main">
  	<a href='https://<%=server %>/inf/exam/logout.html' >Logout</a>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
  </body>
</html>
