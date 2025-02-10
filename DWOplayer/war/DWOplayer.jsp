<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.regex.*" %>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache" %>
<%@ page import="nl.uu.fi.dwo.rest.util.Form" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%! static final long TSTAMP = System.currentTimeMillis(); %>
<%
// 	String responsive = request.getParameter("responsive");
// 	String header = request.getParameter("header");
	String useragent = request.getHeader("user-agent");
	String formfactor = Form.getFormFactor(useragent).name();

	String profile = request.getParameter("profile");
	String name = "";
	if(profile == null||profile.isEmpty()) profile="77";
	else if (!Pattern.matches("\\d+", profile)) {
	  response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  return;
	}
	String dwo_env = System.getProperty("DWO_ENV", "app");
	String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
	String base = request.getParameter("base");
	Pattern legal = Pattern.compile("/[a-z]+(/[a-z]+)*/");
// not too much urls allowed
	if(base == null || base.contains("'") || !legal.matcher(base).matches() ) 
	  base = "";
	else {
	  base = "<script>base='" + base + "' </script>"; 
	}
	try {
		DomDwoProfile dom = PublicProfileCache.get(profile);
		if (dom.getDwoProfileRights().contains("c"))
			name = dom.getDwoProfileName();
	} catch(Exception e) {
		log("get profile failed", e);
	}
%>
<html>
  <head>
    <%=base %>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge"> 
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css?t=<%=TSTAMP%>">
<% if (!name.isEmpty()) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/<%=name %>.css" ><%}%>    
    <meta name="gwt:property" content="locale=nl" >
    <script>
    	DWO_PROFILE_ID = <%=profile%>
    	dwo_env = "<%=dwo_env%>"
    	formfactor = "<%=formfactor%>"
    	
    	function logout() {
    		if (window != window.parent)
    			window.parent.logout();
    	}
    	
    </script>
    
    <title>De Digitale Wiskunde Omgeving | Freudenthal Instituut </title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" src="/dwo/apps/deploy.jsp" ></script>
    <script type="text/javascript" src="/dwo/tablet/DWOplayer/DWOplayer.nocache.js"></script>
  </head>
  <body id="main">
<svg 
	version="1.1"
	xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
	viewBox="0 0 670 100"
	style="width: 670px; height: 100px; fill: rgba(0,0,0,0.0) ; position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"
	width="670px" height="100px">
	<text
		style="font-size: 48px; font-style: normal; font-weight: bold; font-family: Ubuntu; white-space: pre;"
		x="0" y="40">Digitaal toetsen met Numworx</text>
	<text
		style="font-size: 48px; font-style: normal; font-weight: normal; font-family: Ubuntu; white-space: pre;"
		x="0" y="80">Digitaal toetsen met Numworx</text>
	<text style="font-size: 12px; font-style: normal; font-weight: normal; font-family: omega_0; white-space: pre;" x="0" y="17">JHI&lt;C1234567890!%^&amp;*()</text>
</svg>
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
