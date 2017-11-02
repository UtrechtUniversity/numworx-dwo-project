<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%
	String requestHash = request.getHeader("X-SafeExamBrowser-RequestHash");
	if(requestHash == null)
	{
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	Logger.getLogger("toets.jsp").severe("hash = " + requestHash);
	boolean failed = true;
	String[] hashes = { "ccbb7f46b416704eeecfd7cb96c0a51c517c311ca232150ec4411968795053f3", // mac 2.1.2
						"6d2b53bcb6cae8826b8c5fd71afeb97c2b8c4e7f5d75526ca17066b96461c904"  // win 2.1.7
				};
	for(String hash : hashes) {
		if(hash.equals(requestHash)) failed = false;
	}
	if(failed)
	{
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}
	
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css">
    <meta name="gwt:property" content="locale=nl" >
    <script>
    	DWO_PROFILE_ID = 77
    	SECURE_MODE="SEB" // possibly others
    	function logout() {
    		window.location = "https://app.dwo.nl/toets/logout.html"
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
  	<a href='/toets/' >Terug</a> naar startscherm
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
