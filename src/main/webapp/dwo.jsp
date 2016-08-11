<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% Object SERVLET = request.getAttribute("SERVLET");
	if(SERVLET == null)
	{
		response.sendError(response.SC_NOT_FOUND);
		return;
	}
	Object contextPath = request.getContextPath();
%>
<HTML>
<HEAD>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<SCRIPT type="text/javascript" src="<%=contextPath %>/script/frameResize.js"></SCRIPT>
<script type="text/javascript" src="<%=contextPath %>/script/cookie_box.js" ></script>

<%
	Object IDEAS   = request.getAttribute("IDEAS");
	Object guestUser = request.getAttribute("guestUser");
	Object extras   = request.getAttribute("extras");
	if(extras == null) extras = "";
	String language = request.getParameter("language");
	if(language == null) language = (String) request.getAttribute("language");
	Object profile = request.getAttribute("profile");
	String scoViewNr = request.getParameter("scoViewNr");
	if(scoViewNr == null) scoViewNr = "";
	String courseViewNr = request.getParameter("courseViewNr");
	if(courseViewNr == null) courseViewNr = "";
	Object cookies = request.getAttribute("cookies");
	if(cookies == null) cookies = Boolean.FALSE;
// extras: Limited School Access (profiel eigenschap)
	boolean limited = false;
	if("87".equals(profile.toString())) {
		limited = true;
	}
	if("91".equals(profile.toString())) {
		limited = true;
	}
	if (limited) {
		extras = extras.toString() + "<param name='limitedSchoolAccess' value='true' />\n     <param name='schoolAccessProperties' value='schools.properties' />";
	}	
%>
</HEAD>
<BODY	bgcolor="#E6E7E9"
	leftmargin="0"
	topmargin="0"
	onload="javascript:parent.API=API;parent.API_1484_11=API;"

	onBeforeUnload="javascript:API.stop();"
	>
<center>
<APPLET
	code	= "fi.dwo.dwojapplet.domain.DWO"
	width	= "100%"
	height= "100%"
	archive = "../DWOJApplet.jar,wiskopdr.jar"
	mayscript
	id="API"
	name="DWO"
	codebase="<%=contextPath %>/jars/"
	>
	Helaas, Java is niet geinstalleerd op uw computer.
	Klik <a target="_blank" href="http://java.com/nl">
	hier</a> om java te installeren.
	<param name="language" value="<%=language %>" />
	<param name="profile" value="<%=profile %>" />
	<param name="CAS" value="local" />
	<param name="cookies" value="<%=cookies %>" />
	<param name="java_arguments" value="-Djava.net.preferIPv4Stack=true -Xmx1024m -Dhttps.protocols=TLSv1"/>
	<param name="codebase_lookup" value="false">
	<param name="classloader_cache" value="false">
	<param name="IDEAS" value="<%=IDEAS %>" />
	<param name="guestUser" value="<%=guestUser %>" />
	<param name="courseViewNr" value="<%=courseViewNr%>"/>
	<param name="scoViewNr" value="<%=scoViewNr%>" />
<!--
	<param name="jnlp_href" value="<%=contextPath %>/<%=profile%>/<%=language%>/dwo.jnlp" />
-->
	<%=extras %>
</APPLET>
</center>
</BODY>
</HTML>
