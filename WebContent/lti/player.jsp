<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
<%
	String profile = request.getParameter("profile");
	if(profile == null) profile = "77";
%>
	<meta charset="UTF-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/dwo/tablet/DWOplayer.css">
    <meta name="gwt:property" content="locale=nl" >
        <script>
    	function logout() {
    		window.parent.logout()
    	}
    	DWO_PROFILE_ID = <%=profile%>
    </script>
    <script type="text/javascript" src="/dwo/tablet/DWOplayer/DWOplayer.nocache.js"></script>
</head>
<body id="main">
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