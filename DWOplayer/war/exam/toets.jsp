<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%
	String id = request.getParameter("id");
	String requestHash = request.getHeader("X-SafeExamBrowser-RequestHash");
	String configHash  = request.getHeader("X-SafeExamBrowser-ConfigKeyHash");
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");

	String dwo_env = System.getProperty("DWO_ENV", "test");
	String defaultPlace = "";
	if (id != null) {
		try {
			defaultPlace = "cc:" + Long.valueOf(id);
		} catch(Exception e) {}
	}
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/DWOplayer.css">
    <meta name="gwt:property" content="locale=nl" >
    <script>
    	DWO_PROFILE_ID = 77
    	SECURE_MODE="SEB" // possibly others
        dwo_env = "<%=dwo_env%>"
        defaultPlace = "<%= defaultPlace %>"
    	function logout() {
    		window.location = "https://<%=server%>/exam/logout.html"
    	}
    </script>
    <title>Safe Exam Browser</title>
    
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="/DWOplayer/DWOplayer.nocache.js"></script>
  </head>
  <body id="main">
  	<a href='https://<%=server%>/toets/logout.html' >Logout</a>
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
