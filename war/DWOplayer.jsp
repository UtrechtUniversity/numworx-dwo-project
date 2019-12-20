<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.regex.*" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%
// 	String responsive = request.getParameter("responsive");
// 	String header = request.getParameter("header");

	String profile = request.getParameter("profile");
	if(profile == null||profile.isEmpty()) profile="77";
	else if (!Pattern.matches("\\d+", profile)) {
	  response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  return;
	}
	String dwo_env = System.getProperty("DWO_ENV", "app");
	String base = request.getParameter("base");
	Pattern legal = Pattern.compile("/[a-z]+(/[a-z]+)*/");
// not too much urls allowed
	if(base == null || base.contains("'") || !legal.matcher(base).matches() ) 
	  base = "";
	else {
	  base = "<base href='" + base + "'>"; 
	}
%>
<html>
  <head>
    <%=base %>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge"> 
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css">
    <meta name="gwt:property" content="locale=nl" >
    <script>
    	DWO_PROFILE_ID = <%=profile%>
    	dwo_env = "<%=dwo_env%>"
    	
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
	<img src='/dwo/tablet/images/numworx/fontloader.svg' >
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
