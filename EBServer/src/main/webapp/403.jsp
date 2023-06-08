<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><!DOCTYPE html>
<html>
   <head>
      <title>
         Error
      </title>
   </head>
   <body>
      <h1>Er is een fout opgetreden</h1>
 
<p>
<%
	String server = request.getHeader("host");
	Object uri = request.getAttribute("javax.servlet.error.request_uri");
	if ("/toets/toets.jsp".equals(uri)) {
%> 
 	Deze versie van de SafeExamBrowser wordt niet ondersteund.<br>
 	Ga naar <b>https://app.dwo.nl/vo/exam/</b> en download de correctie versie!<br>
	<br>
	Browser <a href='https://<%=server %>/toets/logout.html'>afsluiten</a>.
 
 
<% } else { %> 
 
    <b>The status code is:</b> <%= request.getAttribute("javax.servlet.error.status_code") %><br>
    <b>The URL is:</b> <%= request.getAttribute("javax.servlet.error.request_uri") %><br>    
<% } %>
</p>
   </body>
</html>