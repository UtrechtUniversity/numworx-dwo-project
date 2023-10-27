<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Web Application Starter Project</title>
    <script>
        token = "/dwo/oauth2/entree";
    	endpoint = "/gwtclient/index.html"
    	search = ""
    	hash= ""
    	clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003";
    	<%
// criterium?
    		String with = request.getParameter("with");
    	    String code = request.getParameter("code");
    		if (with == null && code == null) {
    	%>
    		clientId = "";
    	<% }
    	%>
    </script>
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>
  </body>
</html>
