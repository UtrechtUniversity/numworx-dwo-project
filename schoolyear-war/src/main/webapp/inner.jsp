<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css">
    <meta name="gwt:property" content="locale=nl" >
    <base href='/en/he/exam/' >
     <script>
    	DWO_PROFILE_ID = 100
    	SECURE_MODE="KIOSK" // possibly others
        dwo_env = "uusaml"
        defaultPlace = ""
    	function logout() {
    	    	top.window.location = "/dwo/saml/doLogout.jsp?return=/sy/"
     	}
    </script>
    <title>Schoolyear Browser</title>
    
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="/dwo/tablet/DWOplayer/DWOplayer.nocache.js"></script>
  </head>
  <body id="main">
  	<a href='/dwo/saml/doLogout.jsp?return=/sy/' >Logout</a>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
  </body>
</html>