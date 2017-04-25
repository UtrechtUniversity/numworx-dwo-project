<%@ page language="java" contentType="text/plain"
    pageEncoding="ISO-8859-1"%>
<%
	String cdn = System.getProperty("CDNURL", "http://cdn.dwo.nl");
%>
#Property file. It should be located in the working directory of the DWO application.
#resourceURLPath should point to a webserver supplying the applet jar files loaded by WiskOpdr.
#serverUrlPath=https://www.dwo.nl/servlet/dwodsaccess
serverUrlPath=/dwo/
resourceUrlPath=<%=cdn %>/resources
jarUrlPath=<%=cdn %>/jars/
httpAuthentication=BASIC
