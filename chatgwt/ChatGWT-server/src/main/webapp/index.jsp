<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile" %>
<%@ page import="nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
  <head>
    <meta charset=UTF-8>
    <meta name="gwt:property" content="locale=en" >
<%
	String profile = request.getParameter("profile");
	String name = "";
	if(profile == null||profile.isEmpty()) profile="77";
	else if (!Pattern.matches("\\d+", profile)) {
  		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
  		return;
	}
	try {
		DomDwoProfile dom = PublicProfileManager.get(profile);
		if (dom.getDwoProfileRights().contains("c"))
			name = dom.getDwoProfileName();
	} catch(Exception e) {
		log("get profile failed", e);
	}
	String cdn = System.getProperty("CDNURL","https://cdn.dwo.nl");
%>
	<script src="/dwo/apps/deploy.jsp"></script>
	<link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/DWOplayer.css" >
<% if (!name.isEmpty()) {%><link type="text/css" rel="stylesheet" href="<%=cdn%>/apps/css/<%=name %>.css" ><%}%>    
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="ChatGWT.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Berichten</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
	<script src='strophe-1.4.3.js'></script>
    <script src="chatgwt/chatgwt.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

  </body>
</html>
