<%@ page language="java" contentType="text/plain"
    pageEncoding="ISO-8859-1"%>
<%
	String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
	String dwo_env = System.getProperty("DWO_ENV", "app");
	String profileExtension = System.getProperty("PROFILE_EXTENSION","");
	String auth = "BASIC";
	if (dwo_env.contains("saml")) auth = "BEARER";
%>
#Property file. It should be located in the working directory of the DWO application.
#resourceURLPath should point to a webserver supplying the applet jar files loaded by WiskOpdr.
#serverUrlPath=https://app.dwo.nl/servlet/dwodsaccess
serverUrlPath=/dwo/
resourceUrlPath=<%=cdn %>/resources
jarUrlPath=<%=cdn %>/jars/
httpAuthentication=<%=auth%>
dwo_env=<%=dwo_env%>
profileExtension=<%=profileExtension%>